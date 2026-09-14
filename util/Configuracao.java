package util;

/** Centraliza os valores de configuracao visual da transmissao. */
public final class Configuracao {
  private static final long ATRASO_MINIMO_MILISSEGUNDOS = 40L;
  private static final long PASSO_ATRASO_MILISSEGUNDOS = 75L;

  private Configuracao() {
    // Impede a criacao de objetos para uma classe somente de configuracao.
  }

  public static long calcularAtraso(int valorControle) {
    int valorSeguro = Math.max(1, valorControle);
    return ATRASO_MINIMO_MILISSEGUNDOS
        + (valorSeguro - 1) * PASSO_ATRASO_MILISSEGUNDOS;
  }
}
