# TareaDistribuidos
Tarea 1 para el ramo Sistemas Distribuidos

Felipe Vega Valencia 201473511-1
Andrés Huerta Rojo 201473544-8

Para compilar,
ejecutar el comando 'make' en la terminal de ubuntu.

Se deben ejecutar 3 archivos con metodo main para la ejecucion completa del programa:
-MarleyServer
-mainserver
-main

Para correr cada archivo se ejecuta el siguiente comando:

	java <nombre ej: MarleyServer>

El codigo MarleyServer corresponde al servidor para distritos.
El ejecutable mainserver para el servidor central.
El archivo main es para clientes.

Una vez iniciado el programa ingresar los datos solicitados, es importante resaltar que
antes de publicar un titan con la funcion (Publicar Titan) en un servidor distrito, el servidor
central debe estar inicializado, ya que la ID para el titan se obtiene desde este mismo.

Si el paso de mensajes no funciona entre equipos, debe desactivar firewall.

Consideracion, dependiendo del tamaño de la lista, puede que un mensaje pueda o no enviarse, en caso de fallar, cambiar el tamaño
del byte[] que se encuentra en el archivo Tcliente.java en la linea 320.
