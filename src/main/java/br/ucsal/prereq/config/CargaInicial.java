package br.ucsal.prereq.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.ucsal.prereq.model.Documento;
import br.ucsal.prereq.model.GrupoRequerimento;
import br.ucsal.prereq.model.OrigemRegra;
import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.model.Setor;
import br.ucsal.prereq.model.TipoRequerimento;
import br.ucsal.prereq.repository.DocumentoRepository;
import br.ucsal.prereq.repository.RegraTriagemRepository;
import br.ucsal.prereq.repository.SetorRepository;
import br.ucsal.prereq.repository.TipoRequerimentoRepository;

@Component
public class CargaInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaInicial.class);

    private final SetorRepository setorRepository;
    private final DocumentoRepository documentoRepository;
    private final TipoRequerimentoRepository tipoRequerimentoRepository;
    private final RegraTriagemRepository regraTriagemRepository;

    public CargaInicial(SetorRepository setorRepository, DocumentoRepository documentoRepository,
            TipoRequerimentoRepository tipoRequerimentoRepository, RegraTriagemRepository regraTriagemRepository) {
        this.setorRepository = setorRepository;
        this.documentoRepository = documentoRepository;
        this.tipoRequerimentoRepository = tipoRequerimentoRepository;
        this.regraTriagemRepository = regraTriagemRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (tipoRequerimentoRepository.count() > 0) {
            return;
        }
        if (!new ClassPathResource("dados/categorias.csv").exists()) {
            log.warn("Carga inicial não executada: dados/categorias.csv não encontrado");
            return;
        }

        Map<String, Setor> setores = carregarSetores();
        Map<String, Documento> documentos = carregarDocumentos();
        Map<String, TipoRequerimento> categorias = carregarCategorias(setores, documentos);
        int regras = carregarRegras(categorias);

        log.info("Carga inicial: {} setores, {} documentos, {} categorias e {} regras",
                setores.size(), documentos.size(), categorias.size(), regras);
    }

    private Map<String, Setor> carregarSetores() {
        Map<String, Setor> setores = new HashMap<>();
        for (String[] campos : lerCsv("setores.csv")) {
            Setor setor = setorRepository.save(new Setor(campos[0], texto(campos[1])));
            setores.put(setor.getNome(), setor);
        }
        return setores;
    }

    private Map<String, Documento> carregarDocumentos() {
        Map<String, Documento> documentos = new HashMap<>();
        for (String[] campos : lerCsv("documentos.csv")) {
            Documento documento = documentoRepository.save(new Documento(campos[0], texto(campos[1]), texto(campos[2])));
            documentos.put(documento.getNome(), documento);
        }
        return documentos;
    }

    private Map<String, TipoRequerimento> carregarCategorias(Map<String, Setor> setores,
            Map<String, Documento> documentos) {
        Map<String, TipoRequerimento> categorias = new HashMap<>();
        for (String[] campos : lerCsv("categorias.csv")) {
            TipoRequerimento categoria = TipoRequerimento.novo(GrupoRequerimento.valueOf(campos[1]));
            categoria.setNomeNoPortal(campos[0]);
            categoria.setDescricao(texto(campos[2]));
            categoria.setQuandoUsar(texto(campos[3]));
            categoria.setQuandoNaoUsar(texto(campos[4]));
            categoria.setEfeitos(texto(campos[5]));
            categoria.setDocumentos(buscarPorNome(campos[6], documentos, campos[0]));
            categoria.setSetores(buscarPorNome(campos[7], setores, campos[0]));
            categoria.setDataRevisao(LocalDate.now());
            categorias.put(categoria.getNomeNoPortal(), tipoRequerimentoRepository.save(categoria));
        }
        return categorias;
    }

    private int carregarRegras(Map<String, TipoRequerimento> categorias) {
        int salvas = 0;
        for (String[] campos : lerCsv("regras.csv")) {
            RegraTriagem regra = new RegraTriagem(campos[0], OrigemRegra.MANUAL);
            if (!campos[1].isBlank()) {
                TipoRequerimento categoria = categorias.get(campos[1]);
                if (categoria == null) {
                    log.warn("Regra \"{}\" ignorada: categoria \"{}\" não encontrada", campos[0], campos[1]);
                    continue;
                }
                regra.setCategoria(categoria);
            }
            regra.setCanalAlternativo(texto(campos[2]));
            regra.setExplicacao(texto(campos[3]));
            regra.setPrioridade(campos[4].isBlank() ? 0 : Integer.parseInt(campos[4]));
            regra.setDataRevisao(LocalDate.now());
            regraTriagemRepository.save(regra);
            salvas++;
        }
        return salvas;
    }

    private <T> List<T> buscarPorNome(String nomes, Map<String, T> cadastrados, String categoria) {
        List<T> encontrados = new ArrayList<>();
        for (String nome : nomes.split("\\|")) {
            if (nome.isBlank()) {
                continue;
            }
            T item = cadastrados.get(nome.trim());
            if (item == null) {
                log.warn("\"{}\" não encontrado, citado na categoria \"{}\"", nome.trim(), categoria);
            } else {
                encontrados.add(item);
            }
        }
        return encontrados;
    }

    private List<String[]> lerCsv(String arquivo) {
        ClassPathResource recurso = new ClassPathResource("dados/" + arquivo);
        if (!recurso.exists()) {
            log.warn("dados/{} não encontrado", arquivo);
            return List.of();
        }
        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(recurso.getInputStream(), StandardCharsets.UTF_8))) {
            return leitor.lines()
                    .skip(1)
                    .filter(linha -> !linha.isBlank())
                    .map(linha -> Arrays.stream(linha.split(";", -1)).map(String::trim).toArray(String[]::new))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String texto(String valor) {
        return valor.isBlank() ? null : valor;
    }
}
