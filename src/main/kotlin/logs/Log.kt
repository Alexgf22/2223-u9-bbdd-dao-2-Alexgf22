package logs

import java.util.logging.Logger

private val log = Logger.getLogger("Logs")

/**
 * @param tag: String   Etiqueta que se utilizará para identificar
 * el mensaje en los registros de registro.
 * @param msg: String  Mensaje que se va a registrar.
 *
 * Esta función hace uso del objeto log y lo que hace es llamar al método
 * info para poder registrar el mensaje.
 */
internal fun i(tag: String, msg: String) {
    log.info("[$tag]- $msg")
}