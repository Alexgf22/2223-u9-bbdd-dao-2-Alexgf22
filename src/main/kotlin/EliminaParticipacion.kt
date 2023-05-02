import java.sql.DriverManager

/**
 * @property ctfId
 * @property grupoId
 */
class EliminaParticipacion(
    private val ctfId: Int,
    private val grupoId: Int) {

    /**
     * Se establece en primer lugar la conexión con la base de datos H2 a través de la url,
     * usuario y contraseña. Después lo que hacemos es una consulta donde eliminamos de la tabla
     * CTFS la fila donde coincida el id del grupo y el id del ctf que le pasamos por parámetro
     * al constructor de la clase con los id de una de las fila de la tabla, de esa manera
     * eliminamos la participación de ese grupo en concreto. En último lugar, cerramos la sentencia
     * y la conexión con la base de datos.
     */
    fun delete() {

        if(grupoId == ctfId) {
            val conexion = DriverManager.getConnection("jdbc:h2:~/test", "user", "user")
            val statement = conexion.createStatement()

            val query = "DELETE CTFid, grupoid, puntuacion FROM CTFS WHERE grupoid = $grupoId AND CTFid = $ctfId"

            statement.executeUpdate(query)

            statement.close()
            conexion.close()

        }

    }



}