package io.github.lmikoto.githooks;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;

/**
 * @author lmikoto
 */
public class GitHookInstaller {

    /**
     * 本地项目git文件夹名称
     */
    private static final String FILE_NAME_GIT = ".git";

    /**
     * 本地项目git hook文件夹名称
     */
    private static final String FILE_NAME_HOOKS = "hooks";


    /**
     * 本地项目自定义git hook脚本文件夹名称
     */
    private static final String FILE_NAME_LOCAL_HOOKS = "ghooks";

    /**
     * git hook名称
     */
    private String gitHookName;

    /**
     * git hook脚本配置路径
     */
    private String gitHookScriptPath;

    /**
     *  maven plugin 日志输出器
     */
    private Log log;

    public GitHookInstaller(Log log, String gitHookName, String gitHookScriptPath) {
        this.log = log;
        this.gitHookName = gitHookName;
        this.gitHookScriptPath = gitHookScriptPath;
    }

    /**
     * 安装git hook 脚本
     * @throws MojoExecutionException
     */
    public void installGitHook() throws MojoExecutionException {

        File gitHooksFolder = getGitHookFolder();
        checkIsGitRepo(gitHooksFolder);

        File gitHookFile = new File(gitHooksFolder + File.separator + this.gitHookName);

        try {

            if (gitHookFile.exists()) {

                installHookFile(gitHooksFolder);

            } else {
                installHookFile(gitHooksFolder);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error where installGitHook: ", e);
        }
    }

    /**
     * 获取最新的钩子脚本输入流
     *
     * @return InputStream
     * @throws FileNotFoundException
     * @throws MojoExecutionException
     */
    private InputStream getNewGitHookScriptInputStream() throws FileNotFoundException, MojoExecutionException {

        // 尝试从当前项目路径获取
        File localGitHookScript = new File(System.getProperty("user.dir") + File.separator + FILE_NAME_LOCAL_HOOKS
                + File.separator + this.gitHookScriptPath);
        if(localGitHookScript.exists()) {
            log.info("Use local git hook file: " + gitHookScriptPath);
            return new FileInputStream(localGitHookScript);
        }

        InputStream inputStream = GitHookInstaller.class.getClassLoader().getResourceAsStream(this.gitHookScriptPath);
        if(null != inputStream){
            log.info("Use plugin git hook file: " + gitHookScriptPath);
            return inputStream;
        }

        throw new MojoExecutionException("Git hook File not found: " + gitHookScriptPath);
    }

    /**
     * 判断是否git项目
     *
     * @param gitHooksFolder git hook folder
     * @throws MojoExecutionException
     */
    private void checkIsGitRepo(File gitHooksFolder) throws MojoExecutionException {
        if(!gitHooksFolder.exists()) {
            log.error("This is not a git repository, initial git hooks fail...");
            throw new MojoExecutionException("This is not a git repository, initial git hooks fail...");
        }
    }

    /**
     * 安装git hook脚本文件到git仓库中
     *
     * @param gitHooksFolder git hook folder
     * @throws MojoExecutionException
     * @throws FileNotFoundException
     */
    private void installHookFile(File gitHooksFolder) throws MojoExecutionException, FileNotFoundException {
        log.info(String.format("Install git hook[%s]...", this.gitHookName));
        writeFile(gitHooksFolder + File.separator + this.gitHookName, getNewGitHookScriptInputStream());
    }


    private void writeFile(String fileFullPath, InputStream source) throws MojoExecutionException {
        try {
            StringBuilder sourceString = new StringBuilder();
            byte[] b = new byte[1024];
            for (int n; (n = source.read(b)) != -1;)   {
                sourceString.append(new String(b, 0, n));
            }
            FileUtils.fileWrite(fileFullPath, sourceString.toString());
            Runtime.getRuntime().exec("chmod +x " + fileFullPath);
        } catch (IOException e) {
            throw new MojoExecutionException("Error when writeFile", e);
        } finally {
            if(source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File getGitHookFolder() {
        return new File(System.getProperty("user.dir") + File.separator + FILE_NAME_GIT + File.separator + FILE_NAME_HOOKS);
    }
}
