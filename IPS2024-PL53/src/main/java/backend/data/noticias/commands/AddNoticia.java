package backend.data.noticias.commands;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import backend.data.Database;
import backend.data.noticias.ImagenDTO;
import backend.data.noticias.NoticiaDTO;

public class AddNoticia {
    private static final String QUERY = "INSERT INTO NOTICIA VALUES (?, ?, ?)";
    private static final String QUERY2 = "INSERT INTO IMAGEN VALUES (?, ?, ?)";
    private Database db = new Database();
    private NoticiaDTO noticia;
    private List<ImagenDTO> imagenes;

    public AddNoticia(NoticiaDTO noticia, List<ImagenDTO> imagenes) {
        this.noticia = noticia;
        this.imagenes = imagenes;
    }

    public void execute() throws SQLException {
        try (Connection c = db.getConnection();
             PreparedStatement pst = c.prepareStatement(QUERY);
             PreparedStatement pst2 = c.prepareStatement(QUERY2)) {
             
            // Crear el CLOB y almacenar el texto
            Clob texto_noticia = c.createClob();
            texto_noticia.setString(1, noticia.getTexto());

            // Insertar la noticia
            pst.setString(1, noticia.getCodNoticia());
            pst.setString(2, noticia.getTitulo());
            pst.setClob(3, texto_noticia);
            pst.executeUpdate();

            // Insertar imágenes si las hay
            if (!imagenes.isEmpty()) {
                for (ImagenDTO img : imagenes) {
                    pst2.setString(1, img.getCodNoticia());
                    pst2.setString(2, img.getIdImagen());
                    pst2.setString(3, img.getNombre());
                    pst2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
