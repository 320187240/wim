package cn.zzz.wim.listener;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.impl.IdeFrameImpl; // 仅用于获取正确的父组件，避免潜在问题
import com.intellij.ui.awt.RelativePoint;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ParameterIndexMouseListener implements EditorMouseListener {

    // 无需构造函数传递 Project，因为 PsiDocumentManager 可以从 Editor 获取 Project

    @Override
    public void mouseClicked(@NotNull EditorMouseEvent event) {
        Editor editor = event.getEditor();
        Project project = editor.getProject(); // 从 Editor 获取 Project
        if (project == null) { // 防御性编程，确保 Project 存在
            return;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return;
        }

        // 使用事件中的逻辑位置转换为偏移量，更准确对应点击位置
        int offset = editor.logicalPositionToOffset(event.getLogicalPosition());
        PsiElement element = psiFile.findElementAt(offset);
        if (element == null) {
            return;
        }

        PsiMethodCallExpression methodCall = findMethodCall(element);
        if (methodCall != null) {
            PsiExpression[] args = methodCall.getArgumentList().getExpressions();
            for (int i = 0; i < args.length; i++) {
                // 检查点击的偏移量是否在参数的文本范围内
                if (args[i].getTextRange().containsOffset(offset)) { // 使用 containsOffset 更精确
                    showPopup(editor, "参数序号: " + (i + 1), event);
                    return; // 找到后即可返回
                }
            }
        }
    }

    /**
     * 从给定的 PSI 元素开始向上查找，直到找到一个 PsiMethodCallExpression。
     * @param element 起始 PSI 元素
     * @return 找到的 PsiMethodCallExpression，如果未找到则返回 null
     */
    private PsiMethodCallExpression findMethodCall(PsiElement element) {
        PsiElement parent = element; // 从当前元素开始检查，因为它本身也可能在参数列表的括号内
        while (parent != null) {
            if (parent instanceof PsiMethodCallExpression) {
                return (PsiMethodCallExpression) parent;
            }
            // 特殊处理：如果当前元素是参数本身，其父元素可能是 PsiExpressionList
            // 需要继续向上查找 PsiMethodCallExpression
            if (parent instanceof PsiExpressionList && parent.getParent() instanceof PsiMethodCallExpression) {
                return (PsiMethodCallExpression) parent.getParent();
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * 在编辑器中显示一个带有消息的弹出气泡。
     * @param editor  目标编辑器
     * @param message 要显示的消息
     * @param event   鼠标事件，用于定位气泡
     */
    private void showPopup(@NotNull Editor editor, @NotNull String message, @NotNull EditorMouseEvent event) {
        JBPopupFactory factory = JBPopupFactory.getInstance();
        if (factory == null) return; // 防御性检查

        // 使用鼠标事件的坐标来定位气泡，使其更贴近点击位置
        RelativePoint popupPosition = new RelativePoint(event.getMouseEvent());

        // 获取一个合适的父组件用于显示气泡，避免潜在的显示问题
        JComponent parentComponent = editor.getContentComponent();
        if (SwingUtilities.getWindowAncestor(parentComponent) instanceof IdeFrameImpl) {
            // 如果编辑器组件的窗口是IDE框架，通常可以直接使用
        } else {
            // 尝试获取一个更顶层的组件，如IDE的根面板
            JFrame ideFrame = (JFrame) SwingUtilities.getWindowAncestor(editor.getComponent());
            if (ideFrame != null) {
                parentComponent = (JComponent) ideFrame.getRootPane();
            }
        }


        factory.createHtmlTextBalloonBuilder(message, MessageType.INFO, null)
                .setFadeoutTime(3000) // 持续时间稍长一些
                .setCloseButtonEnabled(true) // 添加关闭按钮
                .createBalloon()
                .show(popupPosition, Balloon.Position.below);
    }

    // 以下方法是 EditorMouseListener 接口的一部分，如果不需要可以保持为空实现
    @Override
    public void mousePressed(@NotNull EditorMouseEvent event) {
        // 无需实现
    }

    @Override
    public void mouseReleased(@NotNull EditorMouseEvent event) {
        // 无需实现
    }

    @Override
    public void mouseEntered(@NotNull EditorMouseEvent event) {
        // 无需实现
    }

    @Override
    public void mouseExited(@NotNull EditorMouseEvent event) {
        // 无需实现
    }
}