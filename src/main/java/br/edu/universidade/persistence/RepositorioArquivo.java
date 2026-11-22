package br.edu.universidade.persistence;

import br.edu.universidade.model.Universidade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Repositório responsável pela persistência em arquivo do sistema (Task #13).
 * Grava e carrega o objeto raiz Universidade em data/universidade.dat
 * utilizando serialização nativa do Java (ObjectOutputStream / ObjectInputStream).
 */
public class RepositorioArquivo {

    public static final String ARQUIVO_PADRAO = "data/universidade.dat";

    /**
     * Salva o estado da universidade no arquivo padrão (data/universidade.dat).
     */
    public void salvar(Universidade universidade) throws IOException {
        salvar(universidade, ARQUIVO_PADRAO);
    }

    /**
     * Salva o estado da universidade no caminho especificado.
     */
    public void salvar(Universidade universidade, String caminho) throws IOException {
        if (universidade == null) {
            throw new IllegalArgumentException("A instância de Universidade não pode ser nula.");
        }
        if (caminho == null || caminho.isBlank()) {
            throw new IllegalArgumentException("O caminho do arquivo não pode ser vazio.");
        }

        File arquivo = new File(caminho);
        File diretorioPai = arquivo.getParentFile();
        if (diretorioPai != null && !diretorioPai.exists()) {
            diretorioPai.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            oos.writeObject(universidade);
        }
    }

    /**
     * Carrega o estado da universidade a partir do arquivo padrão.
     * Se o arquivo não existir, retorna uma nova instância vazia de Universidade.
     */
    public Universidade carregar() throws IOException, ClassNotFoundException {
        return carregar(ARQUIVO_PADRAO);
    }

    /**
     * Carrega o estado da universidade a partir do caminho de arquivo especificado.
     * Se o arquivo não existir, retorna uma nova instância vazia de Universidade.
     */
    public Universidade carregar(String caminho) throws IOException, ClassNotFoundException {
        if (caminho == null || caminho.isBlank()) {
            throw new IllegalArgumentException("O caminho do arquivo não pode ser vazio.");
        }

        File arquivo = new File(caminho);
        if (!arquivo.exists()) {
            return new Universidade();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            Object obj = ois.readObject();
            if (obj instanceof Universidade u) {
                return u;
            }
            throw new IOException("O conteúdo do arquivo não é uma instância válida de Universidade.");
        }
    }
}
