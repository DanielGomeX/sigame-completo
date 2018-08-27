package com.followme.model;

import java.io.Serializable;
import java.util.Date;

public class GrupoTrajetoModel implements Serializable {
	
	private String nomeGrupoTrajeto;
	private String localEncontro;
	private String localDestino;
	private String horaSaida;
	private String dataSaidaAndroid;
	private String dataSaidaMysql;
	private Integer idLider;
	private String email;
	private Integer id;
	private String autorizado;
	
	
	public String getAutorizado() {
		return autorizado;
	}
	public void setAutorizado(String autorizado) {
		this.autorizado = autorizado;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getNomeGrupoTrajeto() {
		return nomeGrupoTrajeto;
	}
	public void setNomeGrupoTrajeto(String nomeGrupoTrajeto) {
		this.nomeGrupoTrajeto = nomeGrupoTrajeto;
	}
	public String getLocalEncontro() {
		return localEncontro;
	}
	public void setLocalEncontro(String localEncontro) {
		this.localEncontro = localEncontro;
	}
	public String getLocalDestino() {
		return localDestino;
	}
	public void setLocalDestino(String localDestino) {
		this.localDestino = localDestino;
	}
	public String getHoraSaida() {
		return horaSaida;
	}
	public void setHoraSaida(String horaSaida) {
		this.horaSaida = horaSaida;
	}
	public String getDataSaidaAndroid() {
		return dataSaidaAndroid;
	}
	public void setDataSaidaAndroid(String dataSaidaAndroid) {
		this.dataSaidaAndroid = dataSaidaAndroid;
	}
	public String getDataSaidaMysql() {
		return dataSaidaMysql;
	}
	public void setDataSaidaMysql(String dataSaidaMysql) {
		this.dataSaidaMysql = dataSaidaMysql;
	}
	public Integer getIdLider() {
		return idLider;
	}
	public void setIdLider(Integer idLider) {
		this.idLider = idLider;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	

	
	

}
