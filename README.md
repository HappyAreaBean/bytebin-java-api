# bytebin-java-api
![](https://img.shields.io/nexus/imanity-libraries/org.imanity.paste/bytebin-java-api?label=bytebin-java-api&server=https%3A%2F%2Fmaven.imanity.dev&style=flat-square)

A Simple Java API to use for Bytebin

* [Bytebin](https://github.com/lucko/bytebin) - Made by lucko

## Maven
```
<repositories>
    <repository>
        <id>imanity-repo</id>
        <url>https://maven.imanity.dev/repository/imanity-libraries/</url>
    </repository>
</repositories>

<dependency>
    <groupId>org.imanity.paste</groupId>
    <artifactId>bytebin-java-api</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
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