# Spring Boot API REST: Construa uma API

> Anotações do curso [https://cursos.alura.com.br/course/spring-boot-api-rest](https://cursos.alura.com.br/course/spring-boot-api-rest)
> 

## O que é uma API Rest?

*API REST, também chamada de API RESTful, é uma [interface de programação de aplicações](https://www.redhat.com/pt-br/topics/api/what-are-application-programming-interfaces) (API ou API web) que está em conformidade com as restrições do estilo de **arquitetura REST**, permitindo a interação com serviços web RESTful. **REST é a sigla em inglês para "Representational State Transfer"**, que em português significa transferência de estado representacional. **Essa arquitetura foi criada pelo cientista da computação Roy Fielding**.*

**URI** → **Identificador de recurso**

Aluno(*/alunos*)
Topico(*/topicos*)

**Manipulação de recursos → verbos HTTP**

Get /alunos

Post /alunos

Put /alunos/{id}

Delete /alunos/{id} 

**Representações de Recursos → Media Types**

XML

JSON

**Comunicação Stateless**

Não guarda estado, sem usar sessão para armazenar dados de usuário.

## Primeiro endpoint da API

```java
@Controller
public class TopicosController {

	@RequestMapping("/topicos")
	@ResponseBody
	public List<Topico> lista() {

		Topico topico = new Topico("Duvido", "Duvida com Spring", new Curso("Spring", "Programação"));

		return Arrays.asList(topico, topico, topico);
	}
}
```

 Conforme o código acima, podemos notar a annotation @ResponseBody, essa anotação serve para dizermos ao Spring que queremos retornar a reposta no body e não em alguma página web, como por exemplo uma página do Thymeleaf.

> ***Por padrão, o Spring considera que o retorno do método é o nome da página que ele deve carregar, mas ao utilizar a anotação `@ResponseBody`, indicamos que o retorno do método deve ser serializado e devolvido no corpo da resposta.***
> 

Em seguida instanciamos uma classe de tópico para podermos simular um tópico criado. E retornamos para a requisição uma lista de tópicos.

<aside>
💡 **Arrays.asList é um método static para retornar uma lista de objetos. Nesse exemplo passamos os mesmos três objetos, para ele retornar uma lista de Tópicos.**

</aside>

## Rest Controller & Controller

 Após efetuarmos o primeiro **Controller**, podemos melhorar ele da seguinte forma. Colocamos no código anterior a anotação para indicar ao Spring que o retorno não é uma página e que deve ser retornado no corpo da resposta, para isso usamos a **annotation *@ResponseBody***.

Porém pode ficar muito repetitivo efetuar isso para todo os endpoints. Nesse caso, ao invés de usar o @Controller, podemos utilizar o @RestController, ele já assume por si que o retorno será no corpo da resposta.  Ou seja, não será necessário a annotation ***@ResponseBody***.

<aside>
💡 Usamos o ***@RestController*** para substituir o ***@Controller***, assim por padrão ele já assumirá que a resposta será no corpo da resposta.

</aside>

## DTO (Data Transfer Object) & VO (Value Object)

### Conversão Entity para DTO

Podemos converter da seguinte forma: 

```java
public static List<TopicoDTO> converterParaTopico(List<Topico> topicos) {
		return topicos.stream().map(TopicoDTO::new).collect(Collectors.toList());
}
```

Estamos usando a API de Stream do Java, para podermos fazer um map entre o objeto (Entity) Topico, e retornar uma lista do objeto DTO.

### Spring Data JPA

<aside>
💡 Se adicionarmos um arquivo data.sql no resources o Spring executa os scripts que estão dentro toda vez que inicializar.

</aside>

A partir da versão **2.5** do Spring Boot houve uma mudança em relação à inicialização do banco de dados via arquivo **data.sql**, sendo necessário adicionar uma nova propriedade no arquivo `application.properties` para que ela ocorra sem problemas:

`spring.jpa.defer-datasource-initialization=true`

A propriedade mencionada acima indica à JPA que o arquivo `data.sql` deve ser lido para popular o banco de dados **após** a criação das tabelas.

## Busca com Filtros

![tempsnip.png](Spring%20Boot%20API%20REST%20Construa%20uma%20API%20415f98393c834bd28f91b7f7e1a40c86/tempsnip.png)

Conforme o modelo usado acima para exemplo, ao fazermos uma consulta dos tópicos por curso, precisamos fazer **JOIN com a tabela de Curso**, assim conseguimos pegar os tópicos de determinado curso.

Seguindo essa ideia, no Java fica assim:

**URL da chamada:**

http://localhost:8080/topicos?nomeCurso=java

**Controller:**

```java
@RequestMapping("/topicos")
	public List<TopicoDTO> lista(String nomeCurso) {
		System.out.println(nomeCurso);
		if (nomeCurso == null) {
			List<Topico> topicos = topicoRepository.findAll();
			return TopicoDTO.converterParaTopico(topicos);
		} else {
			List<Topico> topicos = topicoRepository.findByCursoNomeIgnoreCase(nomeCurso);
			return TopicoDTO.converterParaTopico(topicos);
		}
	}
```

Aqui estamos falando o seguinte, caso venha o parâmetro pela requisição, usamos a informação para efetuar o **SELECT**, caso o parâmetro seja igual a nulo faremos a consulta e traremos todos os tópicos de todos os cursos.

**Repository**:

```java
public interface TopicoRepository extends JpaRepository<Topico, Long> {

	List<Topico> findByCursoNomeIgnoreCase(String nomeCurso);

}
```

Notamos que o **Join** é feito, porque o Spring Data JPA entende que o nome escrito no comando acima (***findByCursoNomeIgnoreCase***) pertence a classe Curso.
Ou seja, estamos dizendo ai que queremos efetuar um **JOIN na tabela de Tópico e Curso**, para buscar os Tópicos que diz respeito a um curso especifico. 

<aside>
💡 Supondo que na entidade Topico também tenha um atributo chamado cursoNome. Nesse caso teríamos um problema de ambiguidade. O correto a se fazer nesse caso, é escrever dessa forma : findByCurso_NomeIgnoreCase.
Pois assim indicaríamos ao Spring que o nome que queremos diz respeito ao Curso.

</aside>

## Cadastrando

***@RequestBody*** tem como objetivo **indicar ao Spring que os parâmetros enviados no corpo da requisição devem ser atribuídos ao parâmetro do método.**

Podemos efetuar um cadastro da seguinte forma:

```java
@PostMapping
	public void cadastrar(@RequestBody TopicoForm form) {
		Topico topico = form.converterParaTopico(cursoRepository);
		topicoRepository.save(topico);
	}
```

### Boas práticas Cadastro

<aside>
💡 Como boa prática, usamos o status code 201 - Created, para operação de cadastro.

</aside>

Toda vez que devolver 201 para o client, além do status code, precisa devolver duas coisas:

1 - Um Header com a Location - com a localização desse novo recurso que acabou de ser criado.

2 - No corpo da resposta uma representação desse novo recurso.

<aside>
⚠️ Para métodos `void`, será devolvida uma resposta sem conteúdo, juntamente com o código HTTP 200 (*OK*), caso a requisição seja processada com sucesso.

</aside>

Devido a necessidade de passar a location(URI) via header, precisamos (conforme abaixo) instanciar uma URI, mostrado na linha em destaque.

```java
@PostMapping
	public ResponseEntity<TopicoDTO> cadastrar(@RequestBody TopicoForm form, UriComponentsBuilder uriBuilder) {
		Topico topico = form.converterParaTopico(cursoRepository);
		topicoRepository.save(topico);
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDTO(topico));
	}
```

No return, por se tratar de uma criação de um novo recurso, devolvemos o status code 201, por isso passamos no ResponseEntity o created.
E no body (no corpo da resposta) estamos devolvendo um TopicoDTO.

## Bean Validation

<aside>
⚠️ A partir da versão **2.3.0** do Spring Boot o *Bean Validation* não vem mais incluído automaticamente no projeto.

</aside>

Precisa adicionar no pom.xml →

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Após colocar as annotation na classe beans, colocar no controller o @Valid no parâmetro que será validado. Para o Spring saber que você quer validar aquele objeto.

## Controller Advice

Para criarmos um handler para tratarmos as exceções lançadas o primeiro passo é criar um DTO, que irá conter as informações que iremos devolver ao client.

Nesse exemplo iremos tratar as exceções do Beans Validation, que são emitidas pela classe ***MethodArgumentNotValidException.***

Criado nosso DTO, que deverá ficar algo semelhante a isso:

```java
@Getter
public class ErroDeFormularioDTO {

	private String campo;
	private String erro;

	public ErroDeFormularioDTO(String campo, String erro) {
		this.campo = campo;
		this.erro = erro;
	}
}
```

Precisamos criar a classe interceptadora, o handler.

<aside>
💡 **Handler** *na sua tradução literária significa **domador ou treinador**, o que faz sentido, tendo em vista que, é essa classe que ficará responsável em "treinar" nosso programa para quando determinadas exceções forem lançadas.*

</aside>

Primeiro passo para criação do handler é anotar a classe com a annotation ***@RestControllerAdvice.***

Essa anotação tem como objetivo, capturar as exceções lançadas para que elas recebam um devido tratamento. Você pode pensar nele como um **interceptor de exceções lançadas por métodos anotados com** `RequestMapping` ...

Então nossa classe e método handle() ficará assim: 

```java
@Autowired
private MessageSource messageSource;

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErroDeFormularioDTO> handle(MethodArgumentNotValidException exception) {

		List<ErroDeFormularioDTO> dto = new ArrayList<>();
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		
		fieldErrors.forEach(e -> {
			String mensagem = messageSource.getMessage(e, LocaleContextHolder.getLocale());
			ErroDeFormularioDTO erro = new ErroDeFormularioDTO(e.getField(), mensagem);
			dto.add(erro);
		});

		return dto;

	}
```

1  -  **MessageSource** é uma interface que nos permite trabalhar com a mensagem (***messageSource.getMessage()***) conforme código acima. Além de nos prover uma opção de internacionalização das mensagens de erro. Que mostrarei mais adiante.

2 - Anotamos o método handler com duas principais anotações: 

 2.1 - ***@ResponseStatus -*** Por default o handler retornaria um status code 200, porque ele entende que a exceção esta sendo tratada, mas queremos manter o status code de BadRequest.

2.2 - @**ExceptionHandler** - Indicamos via parametro qual a classe que emite as exceções que serão interceptadas e tratadas. Ex: *`@ExceptionHandler(MethodArgumentNotValidException.class)`*

3 -  Instanciamos nossa lista DTO que contem os atributos campo e erro. Que serão enviado no corpo da resposta.

4 - Instanciamos a lista dos fields(Campos) que não estão válidos de acordo com nossa validação.
Para capturarmos os campos precisamos fazer dessa forma Ex: `exception.getBindingResult().getFieldErrors().`

5 - Depois percorremos a lista de campos com erros, e capturamos as mensagens.

<aside>
💡 O método ***getMessage()*** exige dois parâmetros, o primeiro é a exception e o segundo é o Locale, podemos pegar o Local usando o ***LocaleContextHolder.getLocale(),*** dessa forma podemos internacionalizar a mensagem de erro que será transmitida de volta ao client de acordo com o idioma do sistema usado para fazer o Request*.*

</aside>

6 - Por fim instanciamos nosso DTO que receberá a mensagem e os campos que estão com erro, e retornamos.

## GET

Se colocarmos dessa maneira → 

```java
@GetMapping("/{id}")
public void detalhar(Long id) {		
}
```

O Spring irá entender que o id ira vir no parâmetro via URL. 
ex:  ***http://localhost:8080/topicos?nomeCurso=java***

Queremos que o Spring entenda que iremos mandar pelo path uma variável que deve ser feito o binding. Então anotamos o atributo no parâmetro dessa forma utilizando o ***@PathVariable*** →

```java
@GetMapping("/{id}")
public void detalhar(@PathVariable Long id) {		
}
```

Então, implementamos o repository, dessa forma →

```java
@GetMapping("/{id}")
public TopicoDTO detalhar(@PathVariable Long id) {
	Topico topico = topicoRepository.getById(id);
	return new TopicoDTO(topico);
}
```

No return podemos devolver um new e o DTO**, tendo em vista que o construtor do DTO recebe um objeto do tipo Topico** (que seria nosso entity), sendo assim **é possível converter o Topico que retorna da consulta do Repository para uma classe DTO.**

<aside>
⚠️ Importante lembrar que no DTO só devolvemos valores primitivos,enums e DTOs, nunca uma entidade.

</aside>

Uma boa prática para criação dos DTOs é setar os valores já no construtor. Assim você recebe por parametro a entidade, e faz o binding dos atributos.  Dessa maneira → 

```java
@Getter
public class RespostaDTO {
	
	private Long id;
	private String mensagem;
	private LocalDateTime dataCriacao;
	private String nomeAutor;
	
	public RespostaDTO(Resposta resposta) {
		this.id = resposta.getId();
		this.mensagem = resposta.getMensagem();
		this.dataCriacao = resposta.getDataCriacao();
		this.nomeAutor = resposta.getAutor().getNome();
	}
}
```

<aside>
⚠️ Importante lembrar que, como já estamos atribuindo os valores dos atributos no construtor não precisamos dos métodos Setters, isso adiciona uma camada extra de proteção na aplicação.

</aside>

## PUT

Para efetuarmos a atualização de um recurso podemos usar o verbo **PUT** ou o **PATCH**, porém existe uma discussão a respeito desses dois verbos.

**O PUT é usado quando você vai sobrescrever o recurso.** 

**Já o PATCH tem a ideia de fazer uma pequena atualização, quando você vai alterar só um ou alguns campos.**

Porém, para saber se esta sendo alterado o recurso inteiro ou somente alguns campos precisaria de uma validação, então os desenvolvedores costumam usar o verbo **PUT** mesmo.

Dado isso, também é importante frisar que, por boas práticas, utilizamos um DTO especifico para atualização, para termos **maior controle e flexibilidade a respeito dos atributos que podem ou não**, serem atualizados na nossa aplicação.

Dessa maneira começamos pelo DTO, criamos o DTO com os atributos que são permitidos alterações, e depois criamos um construtor que irá receber via parâmetro o **ID do recurso que queremos atualizar, e o Repository** que esta sendo **injetado no Controller**. Feito isso no construtor fazemos a pesquisa do recurso que queremos atualizar byId, e setamos as atualizações.

Fica assim →

```java
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
```

No Controller →

```java
@PutMapping("/{id}")
@Transactional
	public ResponseEntity<TopicoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form) {
		Topico topico = form.atualizar(id, topicoRepository);
		return ResponseEntity.ok(new TopicoDTO(topico));

	}
```

Com a annotation @Transactional avisamos que ao Spring que deverá ser comitado após a execução do método.
Métodos anotados com `@Transactional` serão executados dentro de um contexto transacional.

E então retornamos ao client um response entity com um status code 200 e no corpo da response um objeto do tipo DTO, contendo as informações do recurso e as informações que foram atualizadas.

**Não é a única forma de fazer, poderia ter uma camada de Services e nele ter o repository.save(), passando via parâmetro a entidade atualizada a ser comitada no banco de dados.**

## DELETE

Para efetuarmos o delete, usamos o verbo delete mesmo. Fica assim →

```java
@DeleteMapping("/{id}")
public ResponseEntity<String> deletar(@PathVariable Long id) {
	topicoRepository.deleteById(id);
	return ResponseEntity.ok().body("Deletado com sucesso"); 
}
```

Caso não deseje retornar nenhuma String informando que o recurso foi deletado ou qualquer outra coisa, pode devolver somente o status code 200. Dessa forma →

```java
@DeleteMapping("/{id}")
public ResponseEntity<?> deletar(@PathVariable Long id) {
	topicoRepository.deleteById(id);
	return ResponseEntity.ok().build(); 
}
```

## Tratamento de erro 404

Para tratamento da exceção lançada quando não encontramos algum recurso, precisamos usar o **findById(), que por padrão retorna um Optional,** ou seja, ele não lança uma exceção caso não encontre o registro na base, porque como o próprio nome já diz ele é opcional.

<aside>
🚨 O método `getOne` lança uma *exception* quando o `id` passado como parâmetro não existir no banco de dados

</aside>

O código fica assim → 

```java
@DeleteMapping("/{id}")
public ResponseEntity<?> deletar(@PathVariable Long id) {
	Optional<Topico> topico = topicoRepository.findById(id);
	if (topico.isPresent()) {
		topicoRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}
		return ResponseEntity.notFound().build();
}
```

Primeiro fazemos a consulta com o findBy. O Optional tem um parâmetro que permite que verifiquemos se o objeto esta presente (***isPresent()***).

Então se ele estiver presente, ele irá efetuar o delete, e retornar o ResponseEntity com o status code 200.

Caso contrário, ele irá retornar o status code 404, recurso not found.

<aside>
💡 Não é interessante devolver *exceptions* e *stack traces* para os clientes, em casos de erros na API Rest.

</aside>

## Continuação

[**Spring Boot API Rest: Segurança da API, Cache e Monitoramento**]
