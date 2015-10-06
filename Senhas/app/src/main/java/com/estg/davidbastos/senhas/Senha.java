package com.estg.davidbastos.senhas;

import java.util.Date;

/**
 * Created by David Bastos on 29-07-2015.
 */
public class Senha {

    private String refeicao;
    private String periodo;
    private Boolean docente;
    private String cantina;
    private String preco;
    private Date data;
    private String telemovel;

    public Senha() {
    }

    public Senha (String refeicao,String periodo,Boolean docente,String cantina,String preco, Date data, String telemovel){
        this.refeicao = refeicao;
        this.periodo = periodo;
        this.docente = docente;
        this.cantina = cantina;
        this.preco = preco;
        this.data = data;
        this.telemovel = telemovel;
    }

    public String getRefeicao() {
        return refeicao;
    }

    public String getPeriodo() {
        return periodo;
    }

    public Boolean getDocente() {
        return docente;
    }

    public String getCantina() {
        return cantina;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public void setRefeicao(String refeicao) {
        this.refeicao = refeicao;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public void setDocente(Boolean docente) {
        this.docente = docente;
    }

    public void setCantina(String cantina) {
        this.cantina = cantina;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getTelemovel() {
        return telemovel;
    }

    public void setTelemovel(String telemovel) {
        this.telemovel = telemovel;
    }
}
