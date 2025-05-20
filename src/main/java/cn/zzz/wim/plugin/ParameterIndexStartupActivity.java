package cn.zzz.wim.plugin;

import cn.zzz.wim.listener.ParameterIndexMouseListener;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParameterIndexStartupActivity implements ProjectActivity {

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // 项目打开时，注册我们的鼠标监听器
        // 将 project 作为 Disposable 传入，当项目关闭时，监听器会自动被移除
        EditorFactory.getInstance().getEventMulticaster().addEditorMouseListener(
                new ParameterIndexMouseListener(),
                project // 使用 project 作为 Disposable
        );
        // ProjectActivity 的 execute 方法需要返回 Unit.INSTANCE (对于 Kotlin 协程)
        // 或者在 Java 中，如果不需要异步操作，直接返回 null 或执行同步代码后返回 Unit.INSTANCE
        return Unit.INSTANCE;
    }
}