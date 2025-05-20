package cn.zzz.wim.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Component;
import java.awt.Point;

public class ParameterIndexAction extends AnAction {

    // 通知组ID，应在 plugin.xml 中定义
    public static final String NOTIFICATION_GROUP_ID = "cn.zzz.wim.parameterindex.notifications";

    /**
     * 当用户通过右键菜单选择此 Action 时调用。
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        final Project project = e.getProject();

        if (editor == null || psiFile == null || project == null) {
            showPopupMessage(project, editor, "无法获取编辑器或文件信息。", MessageType.WARNING, e);
            return;
        }

        // 对于右键菜单触发的 Action，AnActionEvent 中的上下文可能更重要。
        // CaretModel().getOffset() 仍然是当前光标位置。
        // 如果希望基于右键点击的位置，需要从 MouseEvent 获取，但这对于 AnAction 稍微复杂。
        // 通常 AnAction 依赖于 CommonDataKeys.CARET 或 CommonDataKeys.PSI_ELEMENT。
        // 我们这里仍然使用光标位置，因为这是 AnAction 的标准做法。
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = psiFile.findElementAt(offset);

        if (elementAtCaret == null) {
            showPopupMessage(project, editor, "无法在光标位置找到元素。", MessageType.INFO, e);
            return;
        }

        PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethodCallExpression.class, false);

        if (methodCall != null) {
            PsiExpressionList argumentList = methodCall.getArgumentList();
            PsiExpression[] args = argumentList.getExpressions();
            boolean foundArgument = false;
            for (int i = 0; i < args.length; i++) {
                PsiExpression arg = args[i];
                // 检查光标是否在参数的文本范围内
                if (arg.getTextRange().containsOffset(offset)) {
                    showPopupMessage(project, editor, "参数序号: " + (i + 1), MessageType.INFO, e);
                    foundArgument = true;
                    break;
                }
            }
            if (!foundArgument) {
                // 光标在方法调用内，但不在任何具体参数上
                if (argumentList.getTextRange().containsOffset(offset)) {
                    showPopupMessage(project, editor, "光标在参数列表内，但未选中具体参数。", MessageType.INFO, e);
                } else {
                    showPopupMessage(project, editor, "光标不在方法参数上。", MessageType.INFO, e);
                }
            }
        } else {
            showPopupMessage(project, editor, "光标不在方法调用中。", MessageType.INFO, e);
        }
    }

    /**
     * 控制 Action 在右键菜单中的可见性和启用状态。
     */
    @Override
    public void update(@NotNull AnActionEvent e) {

        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        final Project project = e.getProject();

        // 基本条件：必须在编辑器中，有文件，有项目
        if (editor == null || psiFile == null || project == null) {
            e.getPresentation().setEnabled(false);
            return;
        }

        // 只在 Java 文件中可用
         if (!(psiFile instanceof PsiJavaFile)) {
             e.getPresentation().setEnabled(false);
             return;
         }

        // 进一步检查：光标位置是否在一个方法调用表达式内部
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        if (elementAtCaret == null) {
            e.getPresentation().setEnabled(false); // 可见，但不可用
            return;
        }

        PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethodCallExpression.class, false);
        if (methodCall != null) {
            // 检查光标是否在参数列表的文本范围内
            boolean isInArgumentList = methodCall.getArgumentList().getTextRange().containsOffset(offset);
            e.getPresentation().setEnabled(isInArgumentList); // 只有在参数列表内才启用
            e.getPresentation().setVisible(true);
        } else {
            e.getPresentation().setEnabled(false); // 不在方法调用中，则不可用
            // 如果希望在非方法调用中完全隐藏此菜单项，则设置 e.getPresentation().setVisible(false);
            // 通常，在右键菜单中，如果 Action 不适用，使其变灰（setEnabled(false)）比完全隐藏更好。
            e.getPresentation().setVisible(true); // 保持可见，但变灰
        }
    }

    /**
     * 显示气泡或通知。
     * @param e AnActionEvent，可用于获取右键点击的位置信息（如果需要更精确的定位）
     */
    private void showPopupMessage(@Nullable Project project, @Nullable Editor editor, @NotNull String message, @NotNull MessageType messageType, @Nullable AnActionEvent e) {
        JBPopupFactory factory = JBPopupFactory.getInstance();

        if (editor != null && factory != null) {
            RelativePoint popupPoint;
            // 如果 AnActionEvent 包含鼠标事件信息，可以用来更精确地定位气泡
            // InputEvent inputEvent = e != null ? e.getInputEvent() : null;
            // if (inputEvent instanceof MouseEvent) {
            //    popupPoint = new RelativePoint((MouseEvent) inputEvent);
            // } else {
            // 回退到基于光标位置
            LogicalPosition logicalPosition = editor.getCaretModel().getLogicalPosition();
            Point pointInEditor = editor.logicalPositionToXY(logicalPosition);
            Component editorComponent = editor.getContentComponent();
            popupPoint = new RelativePoint(editorComponent, pointInEditor);
            // }

            factory.createHtmlTextBalloonBuilder(message, messageType, null)
                    .setFadeoutTime(3000)
                    .setCloseButtonEnabled(true)
                    .createBalloon()
                    .show(popupPoint, Balloon.Position.below);

        } else if (project != null) {
            NotificationType notificationType;
            if (messageType == MessageType.ERROR) {
                notificationType = NotificationType.ERROR;
            } else if (messageType == MessageType.WARNING) {
                notificationType = NotificationType.WARNING;
            } else { // INFO or any other
                notificationType = NotificationType.INFORMATION;
            }
            Notifications.Bus.notify(new Notification(NOTIFICATION_GROUP_ID, "参数提示", message, notificationType), project);
        } else {
            System.out.println("ParameterIndexAction [" + messageType.toString() + "]: " + message);
        }
    }
}