package com.aaron.javalsp;

import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class LanguageClientImpl implements LanguageClient {

    @Override
    public void telemetryEvent(Object object) {
        System.out.println("遥测事件: " + object);
    }

    @Override
    public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        diagnostics.getDiagnostics().forEach(diagnostic ->
                System.out.println("诊断: " + diagnostic.getMessage())
        );
    }

    @Override
    public void showMessage(MessageParams messageParams) {
        System.out.println("消息: " + messageParams.getMessage());
    }

    @Override
    public void logMessage(MessageParams message) {
        System.out.println("日志: " + message.getMessage());
    }

    @Override
    public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams){
        // 创建一个 CompletableFuture 对象，用于异步返回用户选择的结果
        CompletableFuture<MessageActionItem> result = new CompletableFuture<>();

        // 从请求参数中获取消息内容和可用的动作项
        String message = requestParams.getMessage();
        List<MessageActionItem> actions = requestParams.getActions();

        // 在控制台中显示消息内容和动作项（可以替换为实际的用户界面交互）
        System.out.println("服务器请求显示消息: " + message);

        if (actions != null && !actions.isEmpty()) {
            System.out.println("可用的操作:");
            for (int i = 0; i < actions.size(); i++) {
                System.out.println((i + 1) + ". " + actions.get(i).getTitle());
            }

            // 模拟用户选择动作（这里是通过控制台输入）
            System.out.print("请输入选择的操作编号: ");
            try (Scanner scanner = new Scanner(System.in)) {
                int choice = scanner.nextInt();
                if (choice > 0 && choice <= actions.size()) {
                    // 返回用户选择的动作
                    result.complete(actions.get(choice - 1));
                } else {
                    System.out.println("无效选择，返回空响应。");
                    result.complete(null); // 用户未选择任何动作
                }
            } catch (Exception e) {
                System.out.println("输入错误，返回空响应。");
                result.complete(null); // 用户未选择任何动作
            }
        } else {
            System.out.println("没有可用的操作项，直接返回空响应。");
            result.complete(null); // 没有可选动作
        }

        return result;
    }
}
