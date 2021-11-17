package br.com.demo.forum.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.com.demo.forum.modelo.Topico;
import br.com.demo.forum.repository.TopicoRepository;
import lombok.Data;

@Data
public class AtualizacaoTopicoForm {

	@NotNull
	@NotBlank
	@Length(min = 5)
	private String titulo;

	@NotNull
	@NotBlank
	@Length(min = 5)
	private String mensagem;

	public Topico atualizar(Long id, TopicoRepository topicoRepository) {
		Topico topico = topicoRepository.getById(id);
		topico.setTitulo(this.titulo);
		topico.setMensagem(this.mensagem);
		return topico;
	}

}
