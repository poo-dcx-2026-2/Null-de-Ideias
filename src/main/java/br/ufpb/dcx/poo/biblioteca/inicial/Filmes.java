package br.ufpb.dcx.poo.biblioteca.inicial;

public class Filmes extends Item {
    private int classificacaoIndicativa;
    private boolean disponivel;

    public Filmes(String codigo, String titulo, String autoria, String categoria, int ano, int classificacaoIndicativa) {
        super(codigo, titulo, autoria, categoria, ano);
        this.classificacaoIndicativa = classificacaoIndicativa;
        this.disponivel= true;
    }

    public int getClassificacaoIndicativa() {
        return classificacaoIndicativa;
    }

    public void setClassificacaoIndicativa(int classificacaoIndicativa) {
        this.classificacaoIndicativa = classificacaoIndicativa;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public double calcularPenalidadePorAtraso(int diasDeAtraso){
        if (diasDeAtraso <= 0){
            return 0.0;
        }

        double taxaMultaDiaria = 3.50;
        return diasDeAtraso * taxaMultaDiaria;
    }

    public boolean verificarSeFilmeDisponivel(){
        if (! this.disponivel){
            System.out.println("Este filme está indisponivel no momento!");
            return false;
        }
        return true;
    }

    public void listaEspera( String nomeCliente){
        if (! this.disponivel){
            System.out.println("Filme indisponivel." + nomeCliente + " Foi adicionado a lista de espera!");
        }else {
            System.out.println("O filme está disponivel. Não é necessário entrar na lista de espera! ");

        }
    }

    public boolean limiteEmprestimos(int quantidadedeFilmeAtualCliente){
        int limiteMaximoGeral = 3;
        if (quantidadedeFilmeAtualCliente >= limiteMaximoGeral){
            System.out.println("Limite atingido! O cliente não pode alugar mais filmes!");
            return false;
        }
        return true;
    }


}
