package br.com.demo.forum.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.com.demo.forum.modelo.Curso;
import br.com.demo.forum.modelo.Topico;
import br.com.demo.forum.repository.CursoRepository;
import lombok.Data;

@Data
public class TopicoForm {

	@NotNull @NotBlank @Length(min = 5)
	private String titulo;
	
	@NotNull @NotBlank @Length(min = 5)
	private String mensagem;
	
	@NotNull @NotBlank @Length(min = 5)
	private String nomeCurso;

	public Topico converterParaTopico(CursoRepository cursoRepository) {
		Curso curso = cursoRepository.findByNome(nomeCurso);
		return new Topico(titulo, mensagem, curso);

	}

}
