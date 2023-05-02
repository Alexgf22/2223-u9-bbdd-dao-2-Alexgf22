import java.sql.DriverManager

/**
 * @property grupoId
 */
class InfoGrupo(private val grupoId: Int) {

    /**
     * Se establece en primer lugar la conexión con la base de datos H2 a través de la url,
     * usuario y contraseña. Después lo que hacemos es una consulta donde busca en la tabla
     * GRUPOS el id que le pasamos al constructor de la clase y si lo encuentra en alguna fila
     * imprime únicamente esa fila. En caso de que no haya encontrado ese id hace otra
     * consulta a la base de datos , la cual devuelve ahora todas las filas de la tabla y las
     * imprime en el formato que se pide. En último lugar cerramos la sentencia y la conexión
     * con la base de datos.
     */
    fun listarInfo() {
        val conexion = DriverManager.getConnection("jdbc:h2:~/test", "user", "user")

        val statement = conexion.prepareStatement("SELECT * FROM GRUPOS WHERE grupoid = ?")

        statement.setInt(1, grupoId)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val grupoId = resultSet.getInt("grupoid")
            val grupoDesc = resultSet.getString("grupodesc")
            val mejorCTFId = resultSet.getInt("mejorposCTFid")
            println("Procesado: Listado participación del grupo \"$grupoDesc\"")
            println("GRUPO: $grupoId   $grupoDesc   MEJORCTF: $mejorCTFId")
        } else {
            val allStatement = conexion.prepareStatement("SELECT * FROM GRUPOS")
            val allResultSet = allStatement.executeQuery()

            while (allResultSet.next()) {
                val grupoId = allResultSet.getInt("grupoid")
                val grupoDesc = allResultSet.getString("grupodesc")
                val mejorCTFId = allResultSet.getInt("mejorposCTFid")
                println("Procesado: Listado participación del grupo \"$grupoDesc\"")
                println("GRUPO: $grupoId   $grupoDesc   MEJORCTF: $mejorCTFId")
            }
        }

        statement.close()
        resultSet.close()
        conexion.close()
    }



}