package br.com.demo.forum.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.demo.forum.modelo.StatusTopico;
import br.com.demo.forum.modelo.Topico;
import lombok.Getter;
@Getter
public class DetalhesDoTopicoDTO {
	
	private Long id;
	private String titulo;
	private String mensagem;
	private LocalDateTime dataCriacao;
	private String nomeAutor;
	private StatusTopico status;
	private List<RespostaDTO> respostas;
	
	public DetalhesDoTopicoDTO(Topico topico) {
		this.id = topico.getId();
		this.titulo = topico.getTitulo();
		this.mensagem = topico.getMensagem();
		this.dataCriacao = topico.getDataCriacao();
		this.nomeAutor = topico.getAutor().getNome();
		this.status = topico.getStatus();
		this.respostas = new ArrayList<>();
		
		this.respostas.addAll(topico.getRespostas()
				.stream()
				.map(RespostaDTO::new)
				.collect(Collectors.toList()));
	}
	
}
