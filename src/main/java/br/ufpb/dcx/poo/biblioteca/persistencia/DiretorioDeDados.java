package br.ufpb.dcx.poo.biblioteca.persistencia;

import java.nio.file.Path;

public class DiretorioDeDados {
    private DiretorioDeDados(){}

    public static Path atual(){
        return Path.of(System.getProperty("biblioteca.dados", "dados"));
    }
}
