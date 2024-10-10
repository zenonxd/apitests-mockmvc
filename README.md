<p align="center">
  <img src="https://img.shields.io/static/v1?label=SpringExpert - Dev Superior&message=Testes de API com MockMvc e RestAssured&color=8257E5&labelColor=000000" alt="Testes automatizados na prática com Spring Boot" />
</p>

# Tópicos

- Visão geral sobre testes de API
    - Contextualização 
    - MockMvc
    - RestAssured
- Exercícios MockMvc
- Exercícios RestAssured

# Objetivo

Neste estudo, abordaremos os testes de API usando duas ferramentas: MockMvc e RestAssured.

# Contextualização

Testes de API são testes que executamos diretamente nos endpoints da nossa aplicação (validando comportamentos inesperados), evitando que usuários e as aplicações dessa API recebam resultados inesperados.

Com esses testes podemos avaliar os critérios da API, como sua funcionalidade, desempenho, confiabilidade e segurança.

Esses testes de API são em suma: testes de integração (teste de ponta a ponta, ou seja TODAS as partes da aplicação).

Ou seja, testes de caixa preta (igual vimos no estudo de jacoco). 

Testes de caixa preta como já sabemos, são baseados nos requisitos, de forma que especificamos a entrada e saída da nossa API, visando cobrir cenários importantes.

Usaremos nestes testes, MockMvc e RestAssured.

# MockMvc

Ele fornece um suporte para que possamos testar nossa aplicação spring, encapsulando todos os bans necessários para testar a camada web.

É utilizado de forma ampla para testes de integração.

O teste de integração desempenha um papel importante no ciclo de vida de uma aplicação, verificando o comportamento de ponta a ponta de um sistema.

# RestAssured

Biblioteca utilizada para automatizar testes de API em aplicações, permitindo testar a validar serviços REST de uma forma mais simples.

[Documentação](https://rest-assured.io/)

Para inclusão da dependência, basta incluir a [seguinte dependência](https://mvnrepository.com/artifact/io.rest-assured/rest-assured) no arquivo pom.xml, apresentado na Figura 1 (abaixo):

![alt text](image.png)

## Exemplo básico utilização REST Assured

Para este exemplo, vamos considerar uma API para consultar partidas entre dois times, conforme a Figura 2 (abaixo):

![alt text](image-1.png)

⬆️ Exemplo de API partidas

Considere que o endpoint para consultar esta partida seja http://localhost:8080/events?id=390 

Desta forma, utilizando o Rest Assured podemos fazer uma requisição para o endpoint /events e consultar pelo evento de id = 390. Em seguida, podemos acessar os valores retornados no Json e validá-los.

Na Figura 3 (abaixo), temos um teste em que dada à URI, deve retornar 200 (OK) e em seguida obter o corpo da resposta e validar se o valor leagueId corresponde a 35.

![alt text](image-2.png)

⬆️ Exemplo de teste com RestAssured

# Recursos importantes (TokenUtil e dependência RestAssured)

Classe TokenUtil (MockMvc) Responsável por obter token de acesso;

[Repositório](https://gist.github.com/oliveiralex/faeba65e214f7e6d738c01516ac7d6d2)

Dependência Rest Assured

```pom
<dependency>
	<groupId>io.rest-assured</groupId>
	<artifactId>rest-assured</artifactId>
	<scope>test</scope>
</dependency>
```

Dependência para permitir criar um objeto JSON a partir de um Map

```pom
<dependency>
	<groupId>com.googlecode.json-simple</groupId>
	<artifactId>json-simple</artifactId>
	<version>1.1.1</version>
</dependency>
```

# Exercícios de fixação: Testes de API com MockMvc

## Problema 1: Consultar produto por nome

Implemente o teste de API usando MockMvc para consultar produtos por nome. Desta forma, ao fazer a requisição do tipo GET no endpoint /products?name={productName} onde productName deve ser correspondente a string 
“Macbook” deve retornar como response o status 200 (Ok) e verificar se os campos id, name, price e imgUrl retornados no jsonPath correspondem aos valores da Figura 1 (abaixo).

![alt text](image-3.png)

Primeriamente, criação o pacote controllers > it na pasta de tests com a classe ProductControllerIT.

Passamos as anotações @SpringBootTest e @AutoConfigureMockMvc. Como é um teste de integração, sempre passamos essas anotações para que a aplicação carregue o contexto da mesma.

A terceira anotação será a @Transactional, para sempre que a gente realizar um teste, ele de um rollback no banco de dados para que nada seja alterado de forma permanente.

Depois, importaremos MockMvc com AutoWired.

Aqui, não é um bicho de sete cabeças, teremos dois cenários:

### findAll com nome do produto preenchido

É muito interessante notar a forma que acessamos os parametros retornados do JSON no postman no jsonPath. Entenda o seguinte, se você olhar na imagem ali em cima do retorno do Postman,
ele retorna pra gente em primeiro o "content". Por isso colocamos o [0]! A partir disto, acessamos os parâmetros dentro dessa lista de conteúdos e verificamos o que desejarmos.

```java
    @Test
    public void findAllShouldReturnPageWhenParamIsNotEmpty() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products?name={name}", productName)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(3L));
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[0].price").value(1250.0));
        result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }
```

### findAll com nome não preenchido

![alt text](image-4.png)

Como aqui ele vai retornar uma lista paginada começando pelo primeiro ID, a gente verifica se de fato é que o Postman nos retornaria.

```java
    @Test
    public void findAllShouldReturnPageWhenNameIsEmpty() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(1L));
        result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
        result.andExpect(jsonPath("$.content[0].price").value(90.5));
        result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
    }
```

## Problema 2: Inserir produto

Implemente os testes de API usando MockMvc para inserção de produto (método POST do ProductController), considerando os seguintes cenários. Lembre-se de inserir o 
token no cabeçalho da requisição.

Essa operação é um pouco mais complexa, pois só é permitida para usuários logados que são ADMIN.

Método Controller a ser testado:

```java
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
```

Como podemos ver, ele retorna um ProductDTO com a URI created.

Sabemos que ao rodar a requisição no Postman, ele nos retorna na Header o bearer Token do usuário logado:

![alt text](image-5.png)

Portanto, dentro das requisições abaixo (post), passaremos uma ".header".

Ok, mas e para obter o token de acesso e passar dentro do parâmetro?

Utilizar a Classe TokenUtil (MockMvc), responsável por obter token de acesso, [clique aqui](https://gist.github.com/oliveiralex/faeba65e214f7e6d738c01516ac7d6d2):

Criar uma classe TokenUtil no pacote de Testes e importar todo o código acima.

Essa clase, como é um componente, será importada para nosso ProductControllerIT com AutoWired! E com ela, podemos instanciar token utilizando o MockMvc + username + password.

### setUP

![alt text](image-9.png)

1. Inserção de produto insere produto com dados válidos quando logado como admin

```java
    @Test
    public void insertShouldReturnProductDtoWhenValidDataAndLoggedAsAdmin() throws Exception {

        //converter o DTO para JSON
        String jsonBody = objectMapper.writeValueAsString(productDTO);


        ResultActions result =
                mockMvc.perform(post("/products")
                        // como usamos token, colocamos a header
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                                .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").value(26L));
        result.andExpect(jsonPath("$.name").value("Console Playstation 5"));
        result.andExpect(jsonPath("$.description").value("Lorem ipsum, dolor sit amet consectetur adipisicing elit."));
        result.andExpect(jsonPath("$.price").value(3999.90));
        result.andExpect(jsonPath("$.imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
        //acessando a lista de categorias do JSON
        result.andExpect(jsonPath("$.categories[0].id").value(2L));
    }
```

2. Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo name for inválido

A partir de agora, todos os exercícios voltaram 422 mas em virtude de algo errado, como name (neste caso, price ou algo do tipo). Portanto, a única coisa que muda é o início,
onde settaremos o DTO para o que quisermos.

```java
    @Test
    public void insertShouldReturnUnprocessableEntityWhenInvalidDataLoggedAsAdminAndInvalidName() throws Exception {
        productDTO.setName(" ");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());

    }
```

3. Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo description for inválido

```java
    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminAndInvalidDescription() throws Exception {
        productDTO.setDescription(" ");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        //ResultActions aqui 

        //andExpect o status de isUnprocessableEntity()
    }
```

4. Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo price for negativo

```java
    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminAndPriceNegative() throws Exception {
        productDTO.setPrice(-50.0);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        //ResultActions aqui 

        //andExpect o status de isUnprocessableEntity()
    }
```

5. Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo price for zero

```java
    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminAndPriceIsZero() throws Exception {
        productDTO.setPrice(0.0);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        //ResultActions aqui 

        //andExpect o status de isUnprocessableEntity()
    }
```

6. Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e não tiver categoria associada

```java
    @Test
    public void insertShouldReturnUnprocessableEntityWhenLoggedAsAdminAndNoCategory() throws Exception {
        productDTO.getCategories().clear();
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        //ResultActions aqui 

        //andExpect o status de isUnprocessableEntity()
    }
```

7. Inserção de produto retorna 403 quando logado como cliente

```java
    @Test
    public void insertShouldReturnForbiddenWhenLoggedAsClient() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                //dessa vez, usando token de client
                        .header("Authorization", "Bearer " + clientToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isForbidden());
    }
```

8. Inserção de produto retorna 401 quando não logado como admin ou cliente

```java
    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                mockMvc.perform(post("/products")
                //usando token invalido
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }
```

## Problema 3: Deletar produto

Implemente os testes de API usando MockMvc para deleção de produto (método DELETE do ProductController), considerando os seguintes cenários. Lembre-se de inserir o 
token no cabeçalho da requisição.

Como o delete é por ID, não precisamos passar o ProductDTO no corpo. Portanto, não precisa usar o jsonBody e o ".content e contentType".

1. Deleção de produto deleta produto existente quando logado como admin

```java
    @Test
    public void deleteShouldDoNothingWhenLoggedAsAdminAndProductExists() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }
```

2. Deleção de produto retorna 404 para produto inexistente quando logado como admin

```java
    @Test
    public void deleteShouldReturnNotFoundWhenLoggedAsAdminAndProductDoesnotExists() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
```

3. Deleção de produto retorna 400 para produto dependente quando logado como admin

Nesse caso específico, precisamos colocar o @Transactional

```java
    @Test
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.SUPPORTS)
    public void deleteShouldReturnBadRequestWhenLoggedAsAdminAndProductIdIsDependent() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", dependentId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isBadRequest());
    }
```

4. Deleção de produto retorna 403 quando logado como cliente

```java
    @Test
    public void deleteShouldReturnForbiddenWhenLoggedAsClient() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }
```

5. Deleção de produto retorna 401 quando não logado como admin ou cliente

```java
    @Test
    public void deleteShouldReturnUnauthorizedWhenLoggedAsClient() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }
```

## Problema 4: Consultar pedido por id

Implemente os testes de API usando MockMvc para consulta de pedidos por id (método GET do OrderController), considerando os seguintes cenários. Lembre-se de inserir o token no cabeçalho da requisição.

Lembrar sempre de ver o que retorna na requisição Postman para fazer os expects:

![alt text](image-7.png)

### setUP 

![alt text](image-8.png)

1. Busca de pedido por id retorna pedido existente quando logado como admin

```java
    @Test
    public void findByIdShouldReturnOrderDtoWhenLoggedAsAdmin() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
        //aqui aferimos tudo que o postman retorna
        result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
        result.andExpect(jsonPath("$.status").value("PAID"));
        //a partir daqui, vemos somente se as listas existem
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.payment").exists());
        result.andExpect(jsonPath("$.items").exists());
        result.andExpect(jsonPath("$.total").exists());
    }
```

2. Busca de pedido por id retorna pedido existente quando logado como cliente e o pedido pertence ao usuário

```java
    @Test
    public void findByIdReturnOrderWhenLoggedAsClientAndOrderBelongsToTheUser() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        //aqui aferimos tudo que o postman retorna
        result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
        result.andExpect(jsonPath("$.status").value("PAID"));
        //verificando se o cliente de fato é a Maria
        result.andExpect(jsonPath("$.client.name").value("Maria Brown"));
        result.andExpect(jsonPath("$.payment").exists());
        result.andExpect(jsonPath("$.items").exists());
        result.andExpect(jsonPath("$.total").exists());
    }
```
3. Busca de pedido por id retorna 403 quando pedido não pertence ao usuário (com perfil de cliente)
```java
    @Test
    public void findByIdReturnsForbiddenWhenOrderDoesnotBelongToTheUser() throws Exception {

        ResultActions result =
                // passamos uma ID de outro pedido que sabemos que pertence a outro usuário
                // dará forbidden pois o clientToken nesse teste, pertence à Maria Brown
                mockMvc.perform(get("/orders/{id}", 2L)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isForbidden());
    }
```
4. Busca de pedido por id retorna 404 para pedido inexistente quando logado como admin

```java
    @Test
    public void findByIdShouldReturnNotFoundWhenOrderDontExistLoggedAsAdmin() throws Exception {
        ResultActions result =

                mockMvc.perform(get("/orders/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
```
5. Busca de pedido por id retorna 404 para pedido inexistente quando logado como cliente
```java
   @Test
    public void findByIdShouldReturnNotFoundWhenOrderDontExistAndLoggedAsClient() throws Exception {
        ResultActions result =

                mockMvc.perform(get("/orders/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
```
6. Busca de pedido por id retorna 401 quando não logado como admin ou cliente
```java
    @Test
    public void findByIdShouldReturnUnauthorizedInvalidToken() throws Exception {
        ResultActions result =

                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }
```
