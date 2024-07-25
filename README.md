# bytebin-java-api
[![][maven-badge]](https://repo.fantasyrealms.net/#/releases/cc/happyareabean/paste/bytebin-java-api)

A Simple Java API to use for Bytebin

* [Bytebin](https://github.com/lucko/bytebin) - Made by lucko

## Maven

```xml
<repository>
  <id>fantasyrealms-releases</id>
  <name>Fantasy Realms Repository</name>
  <url>https://repo.fantasyrealms.net/releases</url>
</repository>
```

```xml
<dependency>
  <groupId>cc.happyareabean.paste</groupId>
  <artifactId>bytebin-java-api</artifactId>
  <version>%VERSION%</version>
</dependency>
```

## Usage
```java
public class BytebinTest {

       public static void main(String[] args) throws PasteConnectException {
           PasteFactory pasteFactory = PasteFactory.create("https://your.bytebin.link"); // Create Factory based on your bytebin server
           
           String key = pasteFactory.write("Yoo this is my content!"); // Write content to your bytebin
           String content = pasteFactory.find(key); // Find content by key from your bytebin
           
           System.out.println(content); // result: Yoo this is my content!
       }

}
```

## License
MIT of course

[maven-badge]: https://repo.fantasyrealms.net/api/badge/latest/releases/cc%2Fhappyareabean%2Fpaste%2Fbytebin-java-api?name=bytebin-java-api