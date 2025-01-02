package com.aaron.javalsp;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


@Service
public class CustomClient {

    private static final Logger log = LoggerFactory.getLogger(CustomClient.class);
    private Launcher<LanguageServer> clientLauncher;

    private  OutputStream stdout;
    private InputStream stdin;
    private Process process;
    public void startup(){

        startProcess();
        // 创建客户端实例
        LanguageClientImpl client = new LanguageClientImpl();
        // 使用 LSP4J 的 Launcher 创建客户端与服务端的连接
        clientLauncher = Launcher.createLauncher(client, LanguageServer.class, stdin, stdout);
        // 启动客户端监听
        LanguageServer server = clientLauncher.getRemoteProxy();
        Future<Void> startListening = clientLauncher.startListening();
        // 初始化请求
        InitializeParams initParams = new InitializeParams();
//        initParams.setProcessId(ProcessHandle.current().pid());
        initParams.setRootUri("file:///Users/zxh/code-repo/xxl-job"); // 项目根目录
        initParams.setCapabilities(getCapabilities());

        Map<String,Object> initializationOptions = new HashMap<>();
        Map<String,Object> settings = new HashMap<>();
        Map<String,Object> java = new HashMap<>();
        Map<String,Object> impo = new HashMap<>();
        Map<String,Object> gradle = new HashMap<>();

        gradle.put("enabled","true");
        impo.put("gradle",gradle);
        java.put("import",impo);
        settings.put("java",java);
        initializationOptions.put("settings",settings);

//        initParams.setInitializationOptions(initializationOptions);

        CompletableFuture<InitializeResult> initialize = server.initialize(initParams);
        initialize.thenAccept(result -> {
            log.info("初始化成功，服务器能力：" + result.getCapabilities());
        });

        // 等待客户端启动完成
        try {
            Position position = new Position();
            position.setLine(25);
            position.setCharacter(5);
            ReferenceParams referenceParams = createReferenceParams("/Users/zxh/code-repo/xxl-job/xxl-job-core/src/main/java/com/xxl/job/core/thread/ExecutorRegistryThread.java",position);
            CompletableFuture<List<? extends Location>> completableFuture = server.getTextDocumentService().references(referenceParams);
            completableFuture.thenAccept(result -> {
                // 对结果进行处理
                List<Location> locations = (List<Location>) result;
                log.info("find reference：{}", JSONObject.toJSONString(locations));
            });

            while (true){
                Thread.sleep(10000);
                System.out.println("running");
            }
        }catch (Exception e){
            log.error("error",e);
        }

    }
    public ClientCapabilities getCapabilities() {
        ClientCapabilities capabilities = new ClientCapabilities();

        // Configure textDocument capabilities
        TextDocumentClientCapabilities textDocumentCapabilities = new TextDocumentClientCapabilities();

        // Configure documentSymbol capabilities
        DocumentSymbolCapabilities documentSymbolCapabilities = new DocumentSymbolCapabilities();
        documentSymbolCapabilities.setHierarchicalDocumentSymbolSupport(true);
        textDocumentCapabilities.setDocumentSymbol(documentSymbolCapabilities);

        // Configure publishDiagnostics capabilities
        PublishDiagnosticsCapabilities diagnosticsCapabilities = new PublishDiagnosticsCapabilities();
        diagnosticsCapabilities.setRelatedInformation(false);

//        TagSupportCapabilities tagSupport = new TagSupportCapabilities();
//        tagSupport.setValueSet(new Integer[] {}); // Equivalent to empty list in Java
        DiagnosticsTagSupport diagnosticsTagSupport = new DiagnosticsTagSupport();
        diagnosticsTagSupport.setValueSet(Collections.emptyList());
        diagnosticsCapabilities.setTagSupport(diagnosticsTagSupport);

        diagnosticsCapabilities.setCodeDescriptionSupport(false);
        diagnosticsCapabilities.setDataSupport(false);
        diagnosticsCapabilities.setVersionSupport(false);
        textDocumentCapabilities.setPublishDiagnostics(diagnosticsCapabilities);

        capabilities.setTextDocument(textDocumentCapabilities);

        // Add experimental capabilities
        JsonObject experimentalCapabilities = new JsonObject();
        experimentalCapabilities.addProperty("serverStatusNotification", true);
        capabilities.setExperimental(experimentalCapabilities);

        return capabilities;
    }

    public ReferenceParams createReferenceParams(String filePath, Position position) {
        // 构造 TextDocumentIdentifier
        log.info(URI.create("file://" + filePath).toString());
        TextDocumentIdentifier documentIdentifier = new TextDocumentIdentifier(URI.create("file://" + filePath).toString());

        // 构造 TextDocumentPositionParams
//        TextDocumentPositionParams textDocumentPositionParams = new TextDocumentPositionParams();
//        textDocumentPositionParams.setTextDocument(documentIdentifier);
//        textDocumentPositionParams.setPosition(position);
//        // 构造 WorkDoneProgressParams (默认空)
//        WorkDoneProgressParams workDoneProgressParams = new WorkDoneProgressParams();
//
//        // 构造 PartialResultParams (默认空)
//        PartialResultParams partialResultParams = new PartialResultParams();

        // 构造 ReferenceContext
        ReferenceContext referenceContext = new ReferenceContext();
        referenceContext.setIncludeDeclaration(true);

        // 构造并返回 ReferenceParams
        ReferenceParams referenceParams = new ReferenceParams();
        referenceParams.setPosition(position);
        referenceParams.setTextDocument(documentIdentifier);
//        referenceParams.setWorkDoneProgressParams(workDoneProgressParams);
//        referenceParams.setPartialResultParams(partialResultParams);
        referenceParams.setContext(referenceContext);

        return referenceParams;
    }
    void startProcess(){
        Path workspaceDir = Paths.get("/Users/zxh/code-repo/jdtls_dir/jdtls_workspace");
        try{
            Files.createDirectories(workspaceDir);
            Files.setPosixFilePermissions(workspaceDir, PosixFilePermissions.fromString("rwxrwxrwx"));
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-Declipse.application=org.eclipse.jdt.ls.core.id1",
                    "-Dosgi.bundles.defaultStartLevel=4",
                    "-Declipse.product=org.eclipse.jdt.ls.core.product",
                    "-Dlog.protocol=true",
                    "-Dlog.level=ALL",
                    "-Xmx1g",
                    "--add-modules=ALL-SYSTEM",
                    "--add-opens",
                    "java.base/java.util=ALL-UNNAMED",
                    "--add-opens",
                    "java.base/java.lang=ALL-UNNAMED",
                    "-jar",
                    "/Users/zxh/code-repo/jdtls_dir/jdtls/plugins/org.eclipse.equinox.launcher_1.6.900.v20240613-2009.jar",
                    "-configuration",
                    "/Users/zxh/code-repo/jdtls_dir/jdtls/config_mac",
                    "-data",
                    workspaceDir.toString()
            );
//            ProcessBuilder processBuilder = new ProcessBuilder(
//                    "java","-version"
//            );
//            System.out.println(System.getenv("PATH"));
//            System.out.println(System.getenv("JAVA_HOME"));

            process = processBuilder.start();
//            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            String errorLine;
//            while ((errorLine = errorReader.readLine()) != null) {
//                System.err.println("子进程错误输出: " + errorLine);
//            }
//            int exitCode = process.waitFor();  // 阻塞，直到子进程退出
//            System.out.println("子进程的退出状态码：" + exitCode);
            System.out.println("id:" + process.pid());
            this.stdin = process.getInputStream();
            this.stdout = process.getOutputStream();
        }catch (Exception e){
            System.out.println("error");
        }

    }
}
