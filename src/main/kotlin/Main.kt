import dao.CtfDAO
import dao.GrupoDAO
import dao.entity.CTF
import dao.entity.Grupo
import services.CtfServiceImpl
import services.GrupoServiceImpl
import sql_utils.DataSourceFactory


data class Ctf(val id: Int, val grupoId: Int, var puntuacion: Int)
data class Grupo(val grupoid: Int, val mejorCtfId: Int = 0)

fun main(args: Array<String>) {

    /*
    val participaciones = listOf(
        Ctf(1, 1, 3),
        Ctf(1, 2, 101),
        Ctf(2, 2, 3),
        Ctf(2, 1, 50),
        Ctf(2, 3, 1),
        Ctf(3, 1, 50),
        Ctf(3, 3, 5)
    )
    val mejoresCtfByGroupId = calculaMejoresResultados(participaciones)
    println(mejoresCtfByGroupId)
    */


    // Creamos la instancia de la base de datos
    val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.HIKARI)


    // Creamos la instancia de ctfDao
    val ctfDao = CtfDAO(dataSource)

    // Creamos la instancia de grupoDao
    val grupoDao = GrupoDAO(dataSource)

    // Creamos la instancia de CtfService
    val ctfService = CtfServiceImpl(ctfDao)

    // Creamos la instancia de GrupoService
    val grupoService = GrupoServiceImpl(grupoDao)



    // Operaciones

    if (args.isEmpty()) {
        println("ERROR: El número de parametros no es adecuado.")
    }

    val cmd = args[0]

    when (cmd) {
        // Operación 1
        "-a" -> {
            if(args.size != 4) {
                println("ERROR: El número de parametros no es adecuado.")
            }
            val ctfid = args[1].toInt()
            val grupoid = args[2].toInt()
            val puntuacion = args[3].toInt()
            val nuevoCtf = CTF(ctfid,grupoid,puntuacion)
            ctfService.crearCtf(nuevoCtf)
            println("Procesado: Añadida participación del grupo $grupoid en el CTF $ctfid con una puntuación de $puntuacion puntos.")
        }

        // Operación 2
        "-d" -> {
            if (args.size != 3) {
                println("ERROR: El número de parámetros no es adecuado.")
            }
            val ctfid = args[1].toInt()
            val grupoid = args[2].toInt()
            ctfService.eliminarCtf(ctfid)
            println("Procesado: Eliminada participación del grupo $grupoid en el CTF $ctfid.")
        }

        // Operación 3
        "-l" -> {
            if (args.size > 2) {
                println("ERROR: EL número de parámetros no es adecuado.")
            }
            val grupoid = if (args.size == 2) args[1].toInt() else null

            if (grupoid == null) {
                // Listado de todos los grupos de la tabla
                val grupos = grupoService.obtenerTodosGrupos()
                println("Procesado: Listado de todos los grupos: ")
                for (grupo in grupos) {
                    println("Grupo: ${grupo.grupoid}  ${grupo.grupodesc}  MejorCTF: ${grupo.mejorPosCTFid}")
                }
            }
            else {
                // Listado de una fila de un grupo concreto
                val grupo = grupoService.obtenerGrupo(grupoid)
                if (grupo == null) {
                    println("ERROR: No se ha encontrado el grupo con el id: $grupoid")
                } else {
                    println("Procesado: Listado participación del grupo: ${grupo.grupodesc}")
                    println("Grupo: ${grupo.grupoid}  ${grupo.grupodesc}  MejorCTF: ${grupo.mejorPosCTFid} ")
                }
            }
        }

        else -> {
            println("ERROR: Comando no reconocido.")
        }
    }




}



/**
 * TODO
 *
 * @param participaciones
 * @return devuelve un mutableMapOf<Int, Pair<Int, Ctf>> donde
 *      Key: el grupoId del grupo
 *      Pair:
 *          first: Mejor posición
 *          second: Objeto CTF el que mejor ha quedado
 */
private fun calculaMejoresResultados(participaciones: List<Ctf>): MutableMap<Int, Pair<Int, Ctf>> {
    val participacionesByCTFId = participaciones.groupBy { it.id }
    var participacionesByGrupoId = participaciones.groupBy { it.grupoId }
    val mejoresCtfByGroupId = mutableMapOf<Int, Pair<Int, Ctf>>()
    participacionesByCTFId.values.forEach { ctfs ->
        val ctfsOrderByPuntuacion = ctfs.sortedBy { it.puntuacion }.reversed()
        participacionesByGrupoId.keys.forEach { grupoId ->
            val posicionNueva = ctfsOrderByPuntuacion.indexOfFirst { it.grupoId == grupoId }
            if (posicionNueva >= 0) {
                val posicionMejor = mejoresCtfByGroupId.getOrDefault(grupoId, null)
                if (posicionMejor != null) {
                    if (posicionNueva < posicionMejor.first)
                        mejoresCtfByGroupId.set(grupoId, Pair(posicionNueva, ctfsOrderByPuntuacion.get(posicionNueva)))
                } else
                    mejoresCtfByGroupId.set(grupoId, Pair(posicionNueva, ctfsOrderByPuntuacion.get(posicionNueva)))

            }
        }
    }
    return mejoresCtfByGroupId
}