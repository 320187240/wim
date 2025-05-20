package cn.zzz.wim.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MyToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 创建工具窗口的内容面板 (可以非常简单)
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("点击上方工具栏图标显示参数序号", SwingConstants.CENTER), BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);

        // 为工具窗口添加标题栏动作
        DefaultActionGroup group = new DefaultActionGroup();
        AnAction parameterIndexAction = ActionManager.getInstance().getAction("cn.zzz.wim.action.ParameterIndexAction");
        if (parameterIndexAction != null) { // 总是检查 Action 是否成功获取
            group.add(parameterIndexAction);
        } else {
            System.err.println("Error: Could not find action with ID 'cn.zzz.wim.action.ParameterIndexAction'");
            // 可以添加一个占位符或错误提示 Action
        }


        // 获取 Action 数组
        AnAction[] actionsArray = group.getChildren(null); // 或者 group.getChildren(ActionManager.getInstance())

        // 将数组转换为 List
        List<AnAction> actionsList = Arrays.asList(actionsArray);

        // 设置标题栏右侧的动作
        toolWindow.setTitleActions(actionsList); // <--- 使用转换后的 List

        // 如果想在 gear icon（齿轮图标）菜单中添加，setAdditionalGearActions 接受 ActionGroup
        // toolWindow.setAdditionalGearActions(group); // 这行应该是正确的，因为它期望 ActionGroup
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }
}