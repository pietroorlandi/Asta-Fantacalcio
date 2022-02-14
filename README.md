# Asta-Fantacalcio
Asta Fantacalcio utilizzando la tecnologia Java RMI - Progetto per il corso di Algoritmi Distribuiti

# Esecuzione del sistema

1) Mettersi dentro la cartella "code" ed eseguire lo script "command.sh" attraverso il comando "./command.sh", esso compilerà tutti i file.
2) Avviare il servizio rmiregistri attraverso il comando "rmiregistry &" da terminale e sempre da quel terminale avviare il Server col comando "java -Djava.security.policy=local.java.policy Server"
3) Avviare da 4 terminali diversi 4 client con il comando "java -Djava.security.policy=local.java.policy Client" (il numero di client è personalizzabile modificando una costante nella classe Client)
4) Ora il sistema è funzionante, basterà inserire il nome dei vari client e inizierà l'asta.
