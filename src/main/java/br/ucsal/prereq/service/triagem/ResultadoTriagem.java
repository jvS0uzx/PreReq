package br.ucsal.prereq.service.triagem;

import br.ucsal.prereq.model.RegraTriagem;
import br.ucsal.prereq.model.TipoRequerimento;

public record ResultadoTriagem(TipoRequerimento categoria, String canalAlternativo,
                               RegraTriagem regraAplicada, String mensagem, boolean encontrou) {
}
