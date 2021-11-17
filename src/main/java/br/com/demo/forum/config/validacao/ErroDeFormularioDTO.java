package br.com.demo.forum.config.validacao;

import lombok.Getter;

@Getter
public class ErroDeFormularioDTO {

	private String campo;
	private String erro;

	public ErroDeFormularioDTO(String campo, String erro) {

		this.campo = campo;
		this.erro = erro;
	}

}
