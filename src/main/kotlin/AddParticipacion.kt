import java.sql.DriverManager

/**
 * @property ctfid
 * @property grupodId
 * @property puntuacionNueva
 */
class AddParticipacion(
    private val ctfid: Int,
    private val grupodId: Int,
    private var puntuacionNueva: Int) {


    /**
     * Se establece en primer lugar la conexión con la base de datos H2 a través de la url,
     * usuario y contraseña. Después lo que hacemos es una consulta donde insertamos en la tabla CTFS
     * una nueva fila que correspondería a una nueva participación de un grupo donde le indicamos por
     * parámetro en el constructor de la clase los datos del grupo tanto el id del ctf, como el id del
     * grupo y la puntuación. Se especifica de que tipo es cada dato y se ejecuta la sentencia. Por último
     * se cierra la sentencia y la conexión con la base de datos.
     */
    fun add() {

        if(grupodId == ctfid) {
            val conexion = DriverManager.getConnection("jdbc:h2:~/test", "user", "user")

            val statement = conexion.prepareStatement("INSERT INTO CTFS (CTFid ,grupoid ,puntuacion ) VALUES (?, ?, ?)")
            statement.setInt(1, ctfid)
            statement.setInt(2, grupodId)
            statement.setInt(3, puntuacionNueva)
            statement.executeUpdate()




            statement.close()
            conexion.close()

        }

    }


}