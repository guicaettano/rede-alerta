# Rede Alerta - Simulador da Camada Fisica

![Interface do Rede Alerta](img/preview-rede-alerta.png)

Trabalho pratico de Redes de Computadores I que simula o envio de alertas entre
uma estacao hidrologica e a Central da Defesa Civil. O projeto implementa as
camadas de aplicacao e fisica e transmite cada nivel logico pelo meio de
comunicacao simulado.

## Protocolos implementados

- **Binaria (NRZ-L):** `0` e nivel baixo e `1` e nivel alto.
- **Manchester:** `0` e representado por `01` e `1` por `10`.
- **Manchester Diferencial:** sempre existe uma transicao no meio do bit; o bit
  `0` tambem causa transicao no inicio. O nivel inicial adotado e alto.

A mesma convencao e usada pelo codificador e pelo decodificador.

## Fluxo da simulacao

```text
Aplicacao Transmissora
        v
Camada de Aplicacao Transmissora (String -> quadro de bits)
        v
Camada Fisica Transmissora (codificacao de linha)
        v
Meio de Comunicacao (transferencia bit a bit)
        v
Camada Fisica Receptora (decodificacao de linha)
        v
Camada de Aplicacao Receptora (quadro de bits -> String)
        v
Aplicacao Receptora
```

O quadro e um vetor `int[]`, com um bit (0 ou 1) por elemento e 16 bits
por `char` Java. Os textos do projeto e da interface usam somente caracteres
ASCII simples, sem acentos, cedilha, emojis ou outros simbolos Unicode.

As rotinas, classes e chamadas seguem o framework dos slides. Somente as
lacunas indicadas no PDF foram implementadas; a entrada e a saida usam a GUI
exigida pelo trabalho no lugar de `cin` e `cout`.

## Interface

A interface clara organiza a simulacao como uma cena unica: a mensagem sai da
tela do transmissor, atravessa o meio como uma forma de onda e chega ao receptor.
O molde dos aparelhos e o fundo topografico foram criados para este projeto. A
tela tambem apresenta:

- estacao transmissora e Central da Defesa Civil;
- selecao da codificacao por menu;
- controle da velocidade da simulacao;
- forma de onda e pulso movendo-se da origem ao destino;
- mensagem recuperada pela aplicacao receptora.

## Estrutura

```text
camada-fisica/
--- Principal.java
--- controller/
-   --- ControladorPrincipal.java
--- util/
-   --- Configuracao.java
--- model/
-   --- AplicacaoTransmissora.java
-   --- CamadaAplicacaoTransmissora.java
-   --- CamadaFisicaTransmissora.java
-   --- MeioDeComunicacao.java
-   --- CamadaFisicaReceptora.java
-   --- CamadaAplicacaoReceptora.java
-   --- AplicacaoReceptora.java
--- view/
-   --- view_principal.fxml
-   --- Style.css
--- img/
--- testes/
```

As classes e rotinas das camadas usam nomes em portugues. Identificadores
obrigatorios das APIs Java e JavaFX preservam os nomes definidos pelo framework.

## Requisitos

- Java 8 com JavaFX 8 disponivel. O Oracle JDK 8 tradicional ja inclui JavaFX;
- em distribuicoes OpenJDK 8 que nao incluem JavaFX, e necessario instalar o
  OpenJFX 8 correspondente.

## Compilar e executar no Java 8

Em um JDK 8 com JavaFX 8, os comandos previstos na raiz sao:

```text
javac Principal.java
java Principal
```

Esses dois comandos foram validados em 14/09/2026 com o OpenJDK 8u472 Full,
que inclui JavaFX 8. A compilacao terminou sem erros e a janela abriu
corretamente.

`Principal.java` mantem uma referencia de compilacao ao controller. Dessa
forma, o primeiro comando tambem encontra e compila automaticamente as classes
de `controller`, `model` e `util`, embora o controller seja carregado em tempo
de execucao pelo FXML.

## Execucao alternativa no ambiente de desenvolvimento

No PowerShell, dentro da pasta do projeto:

```powershell
.\executar.ps1
```

O script verifica as versoes de java e javac. Em Java 8 usa o JavaFX do JDK;
em Java modular seleciona JavaFX local com versao principal nao superior ao
JDK. Compila em US-ASCII para `out` e abre a aplicacao. A pasta out e gerada
e pode ser recriada; links nessa pasta sao recusados.

## Verificacao

Veja REVISAO.md para resultados, limites e diferencas em relacao aos slides.
Os testes de regressao estao em testes/TesteCamadas.java e TesteInterface.java.

Para demonstrar mensagens maiores rapidamente, mova a velocidade para
`Rapida`. A posicao inicial `Lenta` foi mantida para permitir a observacao de
cada nivel do sinal, conforme solicitado no projeto.
