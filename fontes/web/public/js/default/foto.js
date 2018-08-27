	/*
	 * Função para que a foto do contato seja pré-visualizada no navegador antes de ser upada.
	 */
	function visualizarFoto() {
	    // Se não suporta a API 'FileReader' já finaliza o uso da função.
	    if(!window.FileReader) {
	        return false;
	    }
	    
	    // Instancia o objeto FileReader
	    oFReader = new FileReader();
	    // Lê a imagem upada
	    oFReader.readAsDataURL(document.getElementById("fileUpload").files[0]);
	
	    // "Pinta" a imagem no elemento de imagem #uploadFoto
	    oFReader.onload = function (oFREvent) {
	        document.getElementById("uploadFoto").src = oFREvent.target.result;
	    };
	};