README
CRY/IO Console 

Como gerar jar ?

Usar o eclipse e colocar todas as libs no jar.

Como Correr ?

O CRY/IO permite que o utilizador defina a pasta local e a pasta na cloud durante o arranque da consola.

java -jar cryio.jar <local dir> <cloud dir>

local dir - Directoria local que armazena os dados não cifrado.
cloud dir - Directoria partilhada que armazena os dados cifrados. 

Como utilizar ?

O CRY/IO foi pensado para ser simples de usar.
São 3 os comandos que o CRY/IO disponibiliza ao utilizador.

Comandos:

Comando: get <nome de um ficheiro presente na cloud dir>
Detalhe: O comando get permite aceder a um ficheiro partilhado. O ficheiro partilhado é armazenadona directoria local de modo a poder editar o ficheiro no seu PC.
Depois de concluida a edição do ficheiro o utilizador pode utilizar o comando put para armazenar o ficheiro na cloud.

Comando: put <nome de um ficheiro presente na local dir>
Detalhe: O comando put permite guardar um ficheiro presente na directoria local na directoria partilhada. 
Este ficheiro está cifrado e assinado pelo cliente.

Comando: exit
Detalhe: Sai da consola.