# git-hook-maven-plugin

## 安装
默认提供了[AngularJS Git Commit Message Conventions](https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit#)的校验规则，当然也可以重写或者添加其他的hooks
```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.github.lmikoto</groupId>
            <artifactId>git-hook-maven-plugin</artifactId>
            <version>1.0.RELEASE</version>
            <executions>
                <execution>
                    <goals>
                        <goal>git-hook</goal>
                    </goals>
                    <configuration>
                        <hooks>
                            <commit-msg>hooks/validate-commit-message.sh</commit-msg>
                        </hooks>
                    </configuration>
                    <phase>compile</phase>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```