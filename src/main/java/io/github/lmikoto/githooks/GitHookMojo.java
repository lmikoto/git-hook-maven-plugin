package io.github.lmikoto.githooks;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author lmikoto
 */
@Mojo(name = "git-hook")
public class GitHookMojo extends AbstractMojo {

    @Parameter
    private Map<String, String> hooks;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if(Objects.nonNull(hooks)){

            for (Map.Entry<String, String> entry : hooks.entrySet()) {

                if (null == entry.getValue() || "".equals(entry.getValue().trim())) {
                    continue;
                }

                if (null != HookTypeEnum.getByType(entry.getKey())) {
                    getLog().info("========== " + entry.getKey() + " excuse" + " ==========");
                    new GitHookInstaller(getLog(), entry.getKey(), entry.getValue()).installGitHook();
                } else {
                    throw new MojoExecutionException(String.format("Git hook type not fund: %s, expected types: %s",
                            entry.getKey(), Arrays.toString(HookTypeEnum.values())));
                }

            }
        }


    }
}
