package br.ufpb.dcx.poo.biblioteca.inicial;

public enum ClassificacaoIndicativa {
    LIVRE(0),SEIS(6),DEZ(10),DOZE(12),QUARTOZE(14),DEZESSEIS(16),DEZOITO(18);

   private int idadeMinima;

    ClassificacaoIndicativa(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public int getIdadeMinima() {
        return idadeMinima;
    }

    private boolean permiteIdade(int idade){
        return idade >= idadeMinima;
    }
}
