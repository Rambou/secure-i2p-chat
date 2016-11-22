# Ασφαλές chat μέσω i2p


Για να τρέξει ο registrar server χρειάζεται η δημιουργία ενός ψηφιακά υπογεγραμένου πιστοποιητικού το οποίο θα υπογραφεί από τον ίδιο τον εκδότη. Για την δημιουργία αυτού του πιστοποιητικού μπορούμε να χρησιμοποιήσουμε το keytool. Αυτό είναι εργαλείο της Java.
```
keytool -genkey -keystore mySrvKeystore -keyalg RSA
```
Στην συνέχεια αφού δημιουργήσουμε το ψηφιακά υπογεγραμμένο από εμάς πιστοποιητικό θα πρέπει να το βάλουμε σε κατάλληλο φάκελο του project μας.

Στην συνέχεια μπορούμε να τον χρησιμοποιήσουμε και να εκτελέσουμε client και registrar με τις εξείς παρακάτω vm παραμέτρους της java.

Για τον Registrar
```
java -Djavax.net.ssl.keyStore=<το όνομα - path, του αρχείου μας> -Djavax.net.ssl.keyStorePassword=<ο κωδικός που βάλαμε> Main
```
Για τον client
```
java -Djavax.net.ssl.trustStore=<το όνομα - path, του αρχείου μας> -Djavax.net.ssl.trustStorePassword=<ο κωδικός που βάλαμε> Main
```

Αν ακόμη θέλουμε να κάνουμε debugg μπορούμε να χρησιμοποιήσουμε τις εξής παραμέτρους στο vm της java.
```
-Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.debug=ssl
```
