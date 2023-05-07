import dao.GrupoCtfDAO
import dao.entity.CTF
import services.GrupoCtfServiceImpl
import sql_utils.DataSourceFactory


/*
data class Ctf(val id: Int, val grupoId: Int, var puntuacion: Int)

data class Grupo(val grupoid: Int, val mejorCtfId: Int = 0)
*/

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


    // Creamos la instancia de GrupoCtfDAO
    val grupoCtfDao = GrupoCtfDAO(dataSource)

    // Creamos la instancia de GrupoCtfServiceImplementación
    val grupoCtfService = GrupoCtfServiceImpl(grupoCtfDao)




    // Operaciones

    /*
    Ejemplo:

    args[0] = "-a"
    args[1] = 1
    args[2] = 2
    args[3] = 100
    */


    if (args.isEmpty()) {
        println("ERROR: El número de parametros no es adecuado.")
    }

    when (args[0]) {
        // Operación 1
        "-a" -> {
            if(args.size != 4) {
                println("ERROR: El número de parametros no es adecuado.")
            }
            else {
                val ctfid = args[1].toInt()
                val grupoid = args[2].toInt()
                val puntuacion = args[3].toInt()

                // Obtenemos el grupo que pertenece el grupoid
                val grupo = grupoCtfService.obtenerGrupo(grupoid)

                if (grupo != null) {
                    /* Comprobamos si el CTF con 'id' ctfid ya cuenta con una participación del grupo
                       con 'id' grupoid
                     */
                    val existeCtf = grupoCtfService.obtenerCtf(ctfid)?.let{it.grupoid == grupoid } ?: false

                    if (existeCtf) {
                        println("ERROR: Ya existe una participación del grupo $grupoid en el CTF $ctfid.")
                    }
                    else {

                        // Creamos si no existe el nuevo Ctf para insertarlo en la tabla
                        val nuevoCtf = CTF(ctfid, grupoid, puntuacion)
                        grupoCtfService.crearCtf(nuevoCtf)

                        /* Actualizar el campo mejorposCTFid de cada grupo en la tabla GRUPOS iterando
                           sobre cada uno.

                         */
                        val grupos = grupoCtfService.obtenerTodosGrupos()
                        for (cadaGrupo in grupos) {
                            grupoCtfService.actualizarMejorPosCtf(cadaGrupo)
                        }

                        println("Procesado: Añadida participación del grupo $grupoid en el CTF $ctfid con una puntuación de $puntuacion puntos.")
                    }

                }
                else {
                    println("ERROR: El grupo $grupoid no existe.")
                }


            }
        }

        // Operación 2
        "-d" -> {
            if (args.size != 3) {
                println("ERROR: El número de parámetros no es adecuado.")
            }
            val ctfid = args[1].toInt()
            val grupoid = args[2].toInt()

            /*
            Se elimina la participación del grupoid en el Ctf con id 'ctfid', después se obtiene
            el grupo con el id específico y si no es nulo se llama ahora a actualizarMejorPosCtf()
            y se le pasa por parámetro el grupo una vez eliminada la participación anteriormente.
             */
            if (grupoCtfService.obtenerCtf(ctfid) != null) {
                grupoCtfService.eliminarCtf(ctfid, grupoid)
                println("Procesado: Eliminada participación del grupo $grupoid en el CTF $ctfid.")

                /* Actualizar el campo mejorposCTFid de cada grupo en la tabla GRUPOS iterando
                   sobre cada uno.

                 */
                val grupos = grupoCtfService.obtenerTodosGrupos()
                for (cadaGrupo in grupos) {
                    grupoCtfService.actualizarMejorPosCtf(cadaGrupo)
                }

            } else {
                println("La participación del grupo $grupoid en CTF $ctfid no existe.")
            }

        }

        // Operación 3
        "-l" -> {
            if (args.size > 2) {
                println("ERROR: EL número de parámetros no es adecuado.")
            }
            val grupoid = if (args.size == 2) args[1].toInt() else null

            if (grupoid == null) {
                // Listado de todos los grupos de la tabla
                val grupos = grupoCtfService.obtenerTodosGrupos()
                println("Procesado: Listado de todos los grupos: ")
                for (grupo in grupos) {
                    println("Grupo: ${grupo.grupoid}  ${grupo.grupodesc}  MejorCTF: ${grupo.mejorPosCTFid}")
                }
            }
            else {
                // Listado de una fila de un grupo concreto
                val grupo = grupoCtfService.obtenerGrupo(grupoid)
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
private fun calculaMejoresResultados(participaciones: List<CTF>): MutableMap<Int, Pair<Int, CTF>> {
    val participacionesByCTFId = participaciones.groupBy { it.CTFid }
    var participacionesByGrupoId = participaciones.groupBy { it.grupoid }
    val mejoresCtfByGroupId = mutableMapOf<Int, Pair<Int, CTF>>()
    participacionesByCTFId.values.forEach { ctfs ->
        val ctfsOrderByPuntuacion = ctfs.sortedBy { it.puntuacion }.reversed()
        participacionesByGrupoId.keys.forEach { grupoId ->
            val posicionNueva = ctfsOrderByPuntuacion.indexOfFirst { it.grupoid == grupoId }
            if (posicionNueva >= 0) {
                val posicionMejor = mejoresCtfByGroupId.getOrDefault(grupoId, null)
                if (posicionMejor != null) {
                    if (posicionNueva < posicionMejor.first)
                        mejoresCtfByGroupId[grupoId] = Pair(posicionNueva, ctfsOrderByPuntuacion[posicionNueva])
                } else
                    mejoresCtfByGroupId[grupoId] = Pair(posicionNueva, ctfsOrderByPuntuacion[posicionNueva])

            }
        }
    }
    return mejoresCtfByGroupId
}