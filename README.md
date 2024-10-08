<p align="center">
  <img src="https://img.shields.io/static/v1?label=SpringExpert - Dev Superior&message=Cobertura de Testes com Jacoco&color=8257E5&labelColor=000000" alt="Testes automatizados na prática com Spring Boot" />
</p>

# Competências

## Parte 1: Avançando nos testes unitários
- Abordagens de teste
  - Caixa branca
  - Caixa preta
- Principais anotações Mockito
  - @Mock
  - @Spy
  - @InjectMocks
  - Exemplo utilização @Mock, @Spy e @InjectMocks

## Parte 2: Cobertura de código com Jacoco

- Tipos básicos de cobertura de código/testes
  - Statement Coverage (Line Coverage)
  - Branch Coverage
  - Function Coverage
- Discussão
- Ferramentas para cobertura
- JaCoCo
  - Dependência
- Recursos importantes
- Exercícios

# Tópicos

# Objetivo

Conforme visto acima, este estudo será divido em duas partes, iremos começar com a primeira a seguir.

# UML

# Parte 1: Avançando nos testes unitários

Essa parte em suma é uma adição do que fora estudado previamente nos testes unitários.

## Abordagens de teste

Uma das partes principais deste processo é o teste de software, cujo objetivo é descobrir sistematicamente diferentes 
classes de erros com uma quantidade de tempo e esforço mínimos. 

Os testes de software podem ser divididos em dois grupos: **caixa branca e caixa preta**

### Caixa branca

Possui este nome, pois o tester tem acesso à estrutura interna da aplicação. Portanto, o seu foco é garantir que os componentes
do software estejam corretos/precisos.

O teste da caixa branca é realizado diretamente no código-fonte, ou seja, ele analisa a estrutura interna dos 
componentes do sistema.

Além disso, nesta técnica, é analisado os caminhos básicos do software e a ideia é que esses caminhos sejam testados.

Um dos principais testes de caixa branca, são os **testes unitários**.

![img.png](img.png)

⬆️ Mostrando o acesso direto ao código-fonte.

### Caixa preta

É baseado nos requisitos básicos do software. O foco é nos requisitos da aplicação, ou seja, **nas ações que ela deve
desempenhar**.

Diferente do caixa branca, os testes aqui **desconsideram o código-fonte no teste**, por isso é chamado de caixa preta.
Portanto, quando utilizamos o teste caixa preta, não nos preocupamos com os elementos constitutivos do software, **nos
preocupamos somente como ele funciona**.

Os principais teste de caixa preta são: **testes de integração** e de **API**.

![img_1.png](img_1.png)

⬆️ Analisando somente os requisitos, não se observa o código-fonte.

### Vantagens

As principais vantagens dos testes unitários é proteger recursos já implementados de serem quebrados a medida que o
código muda. Além disso, proporciona ao desenvolvedor um sendo de proteção da aplicação contra bugs.

# Principais anotações Mockito

## @Mock

Essa anotação em testes unitários é um objeto que implementa o comportamento de algum componente do sistema. Em outras
palavras, **ele vai substituir dependências**. É muito usado para incluir alguma dependência (repository ou service).

## @Spy

A ideia dessa anotação é que nos permita **encapsular a instância de algum objeto existente**. De certa forma, é como
se ele **realmente espionasse algum objeto real**. De uma forma padrão, o Spy irá delegar as chamadas de métodos para 
esse objeto real e rastrear as chamadas e parâmetros.

Ele é usado em circunstâncias bem mais específicas quando comparado ao @Mock, veja:

- Simular comportamento de um método da mesma classe que está sendo testada.
- Testes Unitários em sistemas legados.

## @InjectMocks

Usado para instanciar o objeto testado automaticamente e injetar todas as dependências anotadas @Mock e @Spy.

## Exemplo utilizando @Mock, @Spy e @InjectMocks

Considere um serviço ProductService, que implementa os métodos insert e update de Produtos.

Cada produto é composto pelos campos: id, name e price. Ambos os métodos utilizam uma função ``validateData()``, 
responsável por validar se o nome não é vazio e se o preço é positivo.

Implemente os testes unitários da camada de serviços para os métodos insert e update considerando os cenários de teste.

Observação: Assuma que não estamos usando lib de validação.

[Repositório](https://github.com/devsuperior/poc-example-mock-spy)

Primeira coisa, criar o teste "ProductServiceTests", com a anotação @ExtendWith(SpringExtension.class). Essa anotação diz ao spring para não carregar o contexto da aplicação,
permitindo que usemos os recursos do Spring e JUnit.

Segunda coisa, injetar a classe a ser testada "ProductService", com @InjectMocks.

Após isso, colocar também o Repository a ser usado com @Mock.

O @InjectMocks injeta automaticamente o Repository (com @Mock) dentro da instancia de ProductService que está sendo testado.

Agora, precisamos simular o comportamento do repository (do save e do getReferenceById), que estão sendo usados dentro dos métodos do ProductService.

Para fazer isso, fazemos aquele setUp, montando os possíveis cenários, igual no estudo passado.

Para simular esses cenários, precisamos olhar o comportamento dos métodos. 

O insert, por exemplo, utiliza o save, faremos o seguinte:

1. Instanciaremos um Product e um ProductDTO (fora e depois dentro do setUp)
2. Criaremos o cenário, usando o ``any`` e retornando um Product (afinal, é o que acontece no Service, ele retorna uma entity), veja:

![alt text](image.png)

Agora o getReferenceById é diferente. Se você observar no método do Service, caso o ID não exista ele vai retornar uma exception.

Fazemos o teste de cenário com ID existente e um com não existente (criando um nonExistingId), igual no outro estudo.

![alt text](image-1.png)

Finalizamos o teste de comportamento do Repository e agora, vamos pros métodos em sí utilizando @Spy.

### Testando Insert

No método insert do ProductService, na primeira linha ele já tem o método validateData, passando o DTO. Portanto, já temos um teste de insert com dois cenários, com data válida e inválida.

Data inválida seria: nome vazio, preço nulo ou menor que zero.

Temos um problema. O método validateData está dentro do ProductService, e como ele é protected só pode ser usado por quem está dentro do pacote. Como fazer para simular o seu comportamento? Nós usaremos o Spy, veja abaixo:

#### InsertWithValidData

```java
    @Test
    public void InsertShouldReturnProductDTOWhenValidData() {
        //criando uma variável do tipo ProductService com o Spy, 
        // espionando o service lá de cima com @InjectMocks
        ProductService serviceSpy = Mockito.spy(productService);

        //é um doNothing porque nesse caso, é um caso de sucesso
        Mockito.doNothing().when(serviceSpy).validateData(productDTO);

        //instanciando o resultando, fazendo a ação
        ProductDTO result = serviceSpy.insert(productDTO);

        //verificações
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), "Playstation");
    }
```

#### InsertWithInvalidData (name e price)

##### Name

```java
    @Test
    public void inserShouldReturnInvalidDataExceptionWhenNameIsEmpty() {
        //settando nome nulo
        productDTO.setName("");
        
        //fazendo o Spy novamente
        ProductService serviceSpy = Mockito.spy(productService);

        //dessa vez com doThrow, pois esperamos uma exception
        Mockito.doThrow(InvalidDataException.class)
                .when(serviceSpy).validateData(productDTO);

        //verificando
        Assertions.assertThrows(InvalidDataException.class, () -> serviceSpy.insert(productDTO));
    }
```

##### Price

```java
    @Test
    public void InsertShouldReturnInvalidDataExceptionWhenPriceIsNullOrLessThanZero() {
        productDTO.setPrice(-5.0);
        
        ProductService serviceSpy = Mockito.spy(productService);

        Mockito.doThrow(InvalidDataException.class)
                .when(serviceSpy).validateData(productDTO);

        Assertions.assertThrows(InvalidDataException.class, () -> serviceSpy.insert(productDTO));
    }
```

### Testando Update

O método update recebe dois parâmetros, um Long id e um ProductDTO, ele faz um try-catch e um validateData também.

Temos então, os seguintes cenários:

#### Update com id existente e data válida

```java
    @Test
    public void updateShouldUpdateWhenIdExistsAndValidData() {
        //fazendo o Spy do service novamente parar que possamos acessar a função
        //validateData
        ProductService serviceSpy = Mockito.spy(productService);
        Mockito.doNothing().when(serviceSpy).validateData(productDTO);

        //criando um DTO resultado, usando o Spy e o update
        ProductDTO result = serviceSpy.update(existingId, productDTO);

        //assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingId);

    }
```

#### Update com id existente e data invalida (nome em branco)

```java
    @Test
    public void updateShouldReturnInvalidDataExceptionWhenIdExistsAndProductNameIsBlank() {
        //settando inicialmente nome para nulo
        productDTO.setName("");

        //fazendo spy novamente
        ProductService serviceSpy = Mockito.spy(productService);
        //dessa vez doThrow, pois esperamos uma exception
        Mockito.doThrow(InvalidDataException.class)
                .when(serviceSpy).validateData(productDTO);

        //assertion
        Assertions.assertThrows(InvalidDataException.class, () -> serviceSpy.update(existingId, productDTO));
    }
```

#### Update com id existente e data invalida (preço menor que zero)

```java
    @Test
    public void updateShouldReturnInvalidDataExceptionWhenIdExistsAndProductPriceIsNullOrLessThanZero() {
        productDTO.setPrice(-5.0);
        ProductService serviceSpy = Mockito.spy(productService);
        Mockito.doThrow(InvalidDataException.class)
                .when(serviceSpy).validateData(productDTO);

        Assertions.assertThrows(InvalidDataException.class, () -> serviceSpy.update(existingId, productDTO));
    }
```

#### Update com id inexistente e data valida 

```java
    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExistAndValidData() {

        ProductService serviceSpy = Mockito.spy(productService);
        Mockito.doNothing().when(serviceSpy).validateData(productDTO);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceSpy.update(nonExistingId, productDTO));
    }
```

#### Update com id inexistente e data invalida (nome em branco) 

```java
    @Test
    public void updateShouldReturnInvalidDataExceptionWhenIdDoesNotExistAndProductNameIsBlank() {
        productDTO.setName("");
        ProductService serviceSpy = Mockito.spy(productService);

        Mockito.doThrow(InvalidDataException.class)
                .when(serviceSpy).validateData(productDTO);

        Assertions.assertThrows(InvalidDataException.class, () -> serviceSpy.update(nonExistingId, productDTO));
    }
```

#### Update com id inexistente e data invalida (preço menor que zero) 

```java
    @Test
    public void updateShouldReturnInvalidDataExceptionWhenIdDoesNotExistAndProductPriceIsNullOrLessThanZero() {
        productDTO.setPrice(-5.0);
        ProductService serviceSpy = Mockito.spy(productService);

        Mockito.doThrow(InvalidDataException.class)
                .when(serviceSpy).validateData(productDTO);

        Assertions.assertThrows(InvalidDataException.class, () -> serviceSpy.update(nonExistingId, productDTO));
    }
```

# Parte 2: Cobertura de código com Jacoco

## Introdução à cobertura de código

Quando programamos a ideia é sempre criar aplicações de lata qualidade e livre de falhas, atendendo requisitos (funcionais e não funcionais).

Para que possamos fazer isso de forma correta, uma das partes principais é o teste de software, visando descobrir de forma sistemática diferentes erros com uma quantidade de tempo e esforço mínimo.

Uma das vantagens do testes unitários, por exemplo, é proteger recursos já implementados de serem quebrados à medida que o código muda. Além de proporcionar ao dev um sendo de proteção a aplicação contra bugs.

Porém, algumas pessoas pensam que implementar testes unitários não é o suficiente. Alguns casos, as pessoas defendem a cobertura de código, mas o que seria isso?!

## Cobertura de código

É uma metrica que indica a porcentagem de código que está coberta por ao menos um teste automatizado. Exemplo: Uma cobertura de 90% indica que 10% do código não está coberto por nenhum teste automatizado.

A cobertura de testes é recomendada pois geralmente eliminam possíveis bugs ou permitir que os mesmos sejam descobertas no estágio inicial do desenvolvimento.

Podemos dizer então, que a cobertura de código é uma parte que compõe a cobertura de testes, que é definido como métrica de teste de software que mede a quantidade de testes executados, dado um conjunto de casos de testes.

Enquanto a cobertura de código é uma medida quantitiva (número de linhas de código que foram executadas pelos testes), a cobertura de teste é uma medida QUALITATIVA, permitindo validar a implementação dos requisitos do produto.

Para que possamos realizar essa cobertura de código de maneira correta, precisamos ter acesso a componentes internos (classes e funções) da aplicação.

## Tipos básicos de cobertura de código

### Statement Coverage (Line coverage)

O que é um statement? Uma estrutura condicional por exemplo! (if-else).

É usado para verificar quantas intruções ou comandos são executados. Também é chamado de line coverage por alguns autores.

O cálculo do percentual de statement coverage pode ser calculado da seguinte forma: 

**Statement coverage = Número de statements executados / Número total de statements * 100**

Exemplo:

![alt text](image-2.png)

Retirado de https://www.baeldung.com/cs/code-coverage

Vamos considerar 03 cenários:

1. Para a = 3, b = 5, serão executadas as linhas 1, 2, 3 e 8. Desta forma temos 4 linhas de 8 o que significa que temos 4/8 ou 50% de cobertura.

2. Para a = 3, b = -5, serão executadas as linhas 1, 2, 4, 5 e 8, ou seja 5/8 o que equivale a 63% de cobertura.

3. Para a = 10, b = -10, serão executadas as linhas 1, 2, 4, 6, 7 e 8, ou seja 75%

Assim sendo, para termos uma cobertura de 100% do método soma, todos os 03 cenários devem ser considerados.

#### Vantagens

A vantagem desta abordagem está em permitir verificar diferentes caminhos e quais deles não estão cobertos

### Branch Coverage

Ele verifica se cada ramificação de cada estrutura de controle (incluindo if/else, switch, case, for, while) é executada.

O cálculo do percentual do branch coverage pode ser calculado da seguinte forma:

Branch coverage = Número de branchs executadas / Número total de branchs * 100

Exemplo:

![alt text](image-3.png)

Considerando 02 cenários:

1. Para a = 1, será executada as linhas 1 e 3, ou seja 2/3 equivalente a 75%.

2. Para a = 4, serão executadas as linhas 1-3 ou seja 100%.

Neste caso, ambos os cenários oferecem uma cobertura de 100%.

#### Vantagens

Permite identificar comportamentos não previstos e mapear áreas do código fonte que outras abordagens não mapeiam.

### Function Coverage

A cobertura de função verifica se cada função de um programa está sendo chamada pelo menos uma vez.

Exemplo: No caso de uma aplicação composta por uma única função ou método, a implementação de um único teste de unidade para este método resultará em uma cobertura de 100%.

## Discussão

Qual o percentual ideial de cobertura a ser seguido?

A idea é boa, mas alcançar 100% de cobertura do código não deveria ser uma meta absoluta, pois existem trechos que não precisam diretamente serem testados. Exemplo: Códigos que podemos gerar automaticamente com a própria IDE, como getters e setters.

É uma decisão difícil escolher qual parte do código não precisa ser testado. Mas, caso precise priorizar, teste os métodos que são complicados ou são importantes. Dica: utilize os números de cobertura para identificar trechos que não estão testados.

Alcançar 100% de cobertura é sim desejável, mas não é garantia de que seu sistema seja a prova de defeitos.

## Ferramentas para cobertura

- JaCoCo no contexto do Java;
- Istambul no contexto do Javascript;
- Coverage.py no contexto do Python;
- NCover no contexto do .NET.

Vamos focar no JaCocO!

## JaCoCo

Uma ferramenta open-source usada para mensurar a cobertura de código em aplicações.

A partir de relatórios visuais é possivel identificar as partes do código que estão cobertas e que ainda faltam cobertura.

O JaCoCo implementa 03 métricas principáis para cobertura, sendo:

1. Line Coverage/Statement.

2. Branch Coverage.

3. Cyclomatic Complexity. A partir de uma combinação linear apresenta o número de caminhos que necessitam de cobertura.

O JaCoCo apresenta auxílio ao usuário na visualização e análise da cobertura, usando diamantes coloridos, conforme imagem abaixo:

![alt text](image-4.png)

Retirado de https://www.baeldung.com/jacoco

- Diamante vermelho: Indica que nenhum teste está cobrindo o branch;
- Diamante amarelho: Indica que o código está parcialmente coberto;
- Diamante verde: Indica que todo o branch foi testado e coberto.

### Dependenência

[Link](https://gist.github.com/oliveiralex/fd320a363a4294860b43c8e9bf63ebfc)

### Incluir classes a serem excluídas da cobertura de testes

![alt text](image-5.png)

Retirado de https://www.eclemma.org/jacoco/trunk/doc/maven.html

### Recurso importante (tokenUtil)

Classe TokenUtil, responsável por obter token de acesso.

[Github](https://gist.github.com/oliveiralex/faeba65e214f7e6d738c01516ac7d6d2)

## Fixando tudo. Test coverage + testes unitarios na camada service (DSCommerce)

Para utilizar jacoco no intelij:

1. Importar a dependência acima
2. Você pode colocar manualmente a exclusão de pacotes ou fazer abaixo como vou ensinar
3. Settins >  Build, Execution, Deployment > Coverage e troca para Jacoco

![alt text](image-6.png)

4. Se quiser excluir pacotes e classes:

![alt text](image-7.png)

Excluindo manualmente classes e pacotes:

![alt text](image-8.png)

Assim, ao rodar o projeto e fechar é só gerar o relatório.

## Teste CategoryService por ID 
![alt text](image-11.png)

Mesmo de sempre, criar a pasta Service em Tests com o nome da classe "CategoryServiceTests".

Usamos a anotação ``@ExtendWith(SpringExtension.class)``, para que não seja carregado o contexto da aplicação.

E mockaremos o comportamento do nosso repository.

Primeiro usar o @InjectMocks no CategoryService e depois o @Mock no repository, como já sabemos.

Como o findAll do CategoryService utiliza uma Lista de Category, também importaremos a entidade Category e uma List<Category>.

Para instanciarmos tudo, usaremos o método setUp, como sempre.

Ao invés de instanciar ela manualmente podemos fazer uma Factory. Só criar um CategoryFactory no pacote Factory em Tests.

Essa Factory pode ter uma Category já montada, uma que irá receber parâmetros e até uma CategoryDTO.

![alt text](image-9.png)

E agora no setUp podemos intanciá-la (colocar @BeforeEach no setUp).

![alt text](image-10.png)

Agora, simularemos o comportamento com o Mockito.

![alt text](image-12.png)

E agora, só criar o método. Como vimos na função acima, ela precisa retornar uma lista de CategoryDTO, nosso teste portanto, ficará assim!

![alt text](image-13.png)

## Teste ProductService por ID

![alt text](image-14.png)

Um cenário temos um ID existente e no outro um não existente.

Inicialmente, criar uma class de ProductServiceTests com a anotação do @ExtendWith novamente para não carregar o contexto.

Importar nosso ProductService com @InjectMocks + o ProductRepository com @Mock.

Precisamos mockar o comportamento esperado no setUp novamente.

Como é possivel ver no método nós utilizamos um Id como parâmetro, bem como um Product. Daí é o de sempre, criar um existingId e nonExistingId + uma instância de Product.

Podemos criar uma ProductFactory também. Destacando que o Product possui uma Category, então dentro da ProductFactory, podemos utilizar também a CategoryFactory!

![alt text](image-15.png)

### ProductService (ID existente)

![alt text](image-16.png)

### ProductService (ID inexistente)

Simular o comportamento do repository, usando nonExistingId e retornando um Optional empty.

![alt text](image-17.png)

E dentro do método, fazer a assertion.

![alt text](image-18.png)

### Find Product by name

Sempre observa o comportamento do método que queremos simular.

Método ProductRepository

![alt text](image-19.png)

ProductService

![alt text](image-21.png)

Mock

Instanciar um PageImpl do tipo Product

![alt text](image-22.png)

![alt text](image-23.png)

Método 

![alt text](image-24.png)

### Inserindo new Product

Método ProductService

![alt text](image-25.png)

Mock do save

![alt text](image-26.png)

Método

!! Criar um ProductDTO no setUp

![alt text](image-27.png)

### Update Product

Temos dois cenários, um de Id existente e outro de não existente.

Método Update do Service, passamos um ID + o corpo do que será alterado

![alt text](image-28.png)

Como já mockamos o save, precisamos fazer o do getReferenceById.

Mock

![alt text](image-29.png)

Método (Id existente)

![alt text](image-30.png)

Método (Id inexistente)

![alt text](image-31.png)

## Delete Product

O delete tem o cenário que ele vai deletar com ID existente. Mas também tem as duas exceções (com id não existente e com id com produto dependente, ou seja, linkado em algum pedido).

Método ProductService

![alt text](image-32.png)

Mock

A primeira coisa é fazer os cenários do existsById e depois do deleteById.

![alt text](image-33.png)

Teste

O cenário que o id existe, ele retorna nada, então o método ficará assim:

![alt text](image-34.png)


Se o ID não existe ou é dependente, retornará uma exceção

![alt text](image-35.png)

## Teste carregando usuário (UserService - loadUserByUsername)

Criar uma UserServiceTests com ExtendedWith de novo e trazendo as dependencias: userservice com @InjetectMocks e UserRepository com @Mock.

Método UserService

![alt text](image-36.png)

Podemos criar um existingUsername e nonExistingUsername e um User. Como o método usa uma List<UserDetailsProjection>, também criaremos uma! Iniciar tudo no setUp (pode criar um UserFactory btw).

Lembrar que temos dois tipos de usuário: cliente e admin.

![alt text](image-37.png)

Criar também um UserDetailsFactory para retornar uma lista de UserDetailsProjection. Nela, faremos um pouco diferente das outras factories.

Teremos nosso método de create, mas embaixo (fora do escopo), criaremos uma classe que irá implementar a interface (UserDetailsProjection), veja:

Método create (um para um Client, outro pra Admin e o terceiro para admin e client)

![alt text](image-39.png)

Classe fora do escopo, tudo da UserDetailsProjection, criando um construtor + getters.

![alt text](image-40.png)

Testes

![alt text](image-41.png)

## Melhorias método autenticated (UserService)

Método 

![alt text](image-42.png)

Faremos o seguinte: o primeiro bloco do Try, tudo que está dentro dele, iremos alocar para uma nova classe chamada "CustomUserUtil" no pacote Util (essa classe será um @Component).

![alt text](image-43.png)

Incluindo no UserService:

Importa o component criado

![alt text](image-44.png)

E coloca no bloco do Try

![alt text](image-45.png)

## Testando método autenticated

### User existe

Como pode observar no método acima, dentro do try temos um cenário do repository executando o findByEmail (passando o username). Precisamos testar, portanto, este cenário em pauta.

Mock

![alt text](image-46.png)

Teste

Precisamo mockar o CustomUserUtil criado anteriormente, para que possamos fazer o teste unitário.

![alt text](image-47.png)

Bom, sempre que queremos MOCKAR algo, usamos o mockito, faremos o seguinte:

Esse mockito é para simular isso aqui:

![alt text](image-48.png)

Ou seja, em um cenário de sucesso, ele retorna um username

![alt text](image-49.png)

### User não existe

![alt text](image-50.png)

## Consultando user logado (getMe)

Método

![alt text](image-51.png)

Teste

### user existe

Precisamos fazer um spy do UserService para que possamos utilizar do método. Caso fizéssemos da forma normal, daria um erro dizendo que o "service" passado no when não é um Mock.

![alt text](image-52.png)

### user não existe

![alt text](image-53.png)

## AuthService - ValidateSelfOrAdmin

É um controle de acesso para determinar se um usuário pode ou não acessar um pedido.

![alt text](image-54.png)

### Deixando o método mais testável

Vamos tirar esse If e melhorar o código.

![alt text](image-55.png)

### Teste

Criar classe AuthServiceTests (@ExtendWith), inserir o AuthService com @InjectMocks. Como a classe usa o UserService, injetar ele com @Mock.

Criar algumas variáveis para o User, para que possamos simular os cenários: admin, selfClient, otherClient e iniciá-las no setUp (utilizando a UserFactory).

![alt text](image-56.png)

A partir disso, temos três cenários para o método acima:

1. Acessando pedido como admin

![alt text](image-57.png)

2. Client acessando o proprio pedido

![alt text](image-58.png)

3. Client acessando o pedido que não é dele (ForbiddenException)

## OrderService

Criar OrderServiceTests, mesma coisa. @ExtendWith, @InjectMocks no OrderService e @Mock no OrderRepository e AuthService.

### findById

Temos duas situações. O pedido pode existir ou não.

Uma outra coisa: esse método usa o validateSelfOrAdmin para validar o acesso aos pedidos. Ou seja, se for admin, pode acessar todos os pedidos, se for client somente os próprios pedidos.

![alt text](image-59.png)

Precisamos criar um ID de order que existe e outro que não existe.

Criar também um Order e um OrderDTO, afinal é o que o metodo retorna (criar um OrderFactory).

Como precisamos validar o acesso aos pedidos, criaremos também os 03 (três) tipos de usuários.

OrderFactory:

![alt text](image-60.png)

Iniciar tudo no setUp + criacao dos cenários:

![alt text](image-61.png)

Métodos

Id exists (Admin logged)

![alt text](image-62.png)

Id Exists (Client logged)

![alt text](image-63.png)

Id Exists (Acessando pedido de outro usuário)

![alt text](image-64.png)

Id não existe (ResourceNotFound)

![alt text](image-65.png)

### Insert

![alt text](image-66.png)

Mockaremos o UserService, o do Product (getReferenceById), save e saveAll.

Adicionar o ProductRepository, OrderItemRepository e UserService com o @Mock.

Criar um existingProductId, nonExistingProductId e um Product utilizando a Factory.

![alt text](image-67.png)

setUp, instanciando os atributos criados + os cenários do getReferenceById.

![alt text](image-68.png)

Inserir no setUp também o save e o saveAll!

![alt text](image-69.png)

### Cenários de sucesso - Usuário autenticado

Com Admin (mockar o autenticated)

![alt text](image-70.png)

Com Client

![alt text](image-71.png)

### Cenários de falha - Usuário não autenticado ou pedido não existe

Se o User não tiver autenticado lançará UserNotFoundException

![alt text](image-72.png)


