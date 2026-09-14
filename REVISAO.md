# Revisao tecnica e correcoes

## Relacao com os slides

O PDF fornece pseudocodigo com lacunas, nao um projeto Java compilavel completo.
Foram preservados os nomes das rotinas e o encadeamento entre camadas:

- AplicacaoTransmissora -> CamadaDeAplicacaoTransmissora.
- CamadaDeAplicacaoTransmissora -> CamadaFisicaTransmissora.
- CamadaFisicaTransmissora -> switch 0/1/2 -> MeioDeComunicacao.
- MeioDeComunicacao -> CamadaFisicaReceptora.
- CamadaFisicaReceptora -> switch 0/1/2 -> CamadaDeAplicacaoReceptora.
- CamadaDeAplicacaoReceptora -> AplicacaoReceptora.

As tres sub-rotinas de cada lado continuam separadas. Entrada e saida de
console foram adaptadas a GUI exigida. As conversoes e os algoritmos preenchem
somente as lacunas dos slides. No receptor, os metodos usam Decodificacao para
corresponder as chamadas do slide.

## Problemas corrigidos

- Pacote control renomeado para controller e classe ControladorPrincipal.
- Conversao da String dentro da camada de aplicacao transmissora; reconstrucao
  dentro da camada de aplicacao receptora.
- Um bit por elemento do int[], usando os 16 bits de cada char Java.
  Essa largura e uma convencao desta implementacao, nao uma exigencia do PDF.
- Meio transfere exatamente o comprimento do vetor, sem sentinela de zeros.
- Binaria retorna o mesmo vetor sem transformacao adicional.
- Manchester e diferencial validam pares recebidos; diferencial inicia alto.
- Atraso armazenado em campo volatile: o worker nao consulta o Slider.
- Thread daemon, cancelamento no fechamento e interrupcao respeitada.
- Falhas retornam a interface a um estado utilizavel; callbacks ignorados
  depois do fechamento.
- Janela minima ampliada; velocidade inicial lenta, com 490 ms por nivel.
- Medicao hidrologica rotulada como ilustrativa.
- Scripts compartilham configuracao de execucao e compilacao; verificam java
  e javac; suportam caminho nao modular para JDK 8 com JavaFX incluido.
- Limpeza de out valida o diretorio e recusa links antes da remocao.

## Verificacao executada

Ambientes: OpenJDK 8u472 Full com JavaFX 8 e JDK 24 com JavaFX 24.0.2,
ambos no Windows.

- `javac Principal.java` executado com sucesso no Java 8.
- `java Principal` abriu a interface sem erros no Java 8.
- Compilacao US-ASCII pelo script bem-sucedida.
- TesteCamadas: 3015 ciclos de ida e volta, incluindo mensagem vazia,
  mensagens conhecidas e 1000 mensagens pseudoaleatorias em ASCII.
- Vetores conhecidos de Manchester e diferencial conferidos.
- Rejeicao de pares invalidos/incompletos e quadro de aplicacao incompleto.
- TesteInterface: FXML carregado, envio real nas tres opcoes, mensagem final,
  desbloqueio de controles, ausencia dos paineis removidos e fim da thread ao
  cancelar.
- O runtime emitiu aviso de API interna do JavaFX 24 (sun.misc.Unsafe);
  os testes terminaram com codigo zero.

Os testes ficam em testes/ e nao fazem parte da compilacao de Principal.java.
Para repetir apos compilar o projeto, use o mesmo classpath/module-path JavaFX
da compilacao para compilar e executar testes.TesteCamadas e TesteInterface.

## Limites

O PDF apresenta um framework com lacunas, portanto as conversoes e as tres
codificacoes necessariamente foram implementadas. A GUI substitui somente a
entrada e a saida de console mostradas como referencia nos slides.

O projeto continua sendo um simulador: nao recebe sensores nem usa uma rede
fisica real. Os nomes obrigatorios das APIs Java e JavaFX permanecem como
definidos por essas bibliotecas; os identificadores proprios estao em portugues.

## ASCII

Fontes, testes, FXML, CSS, scripts, Markdown e SVGs textuais usam somente
ASCII. A interface tambem nao exibe acentos, cedilha, emojis ou simbolos
Unicode. PDF e PNG sao arquivos binarios e ficam fora da verificacao textual.
