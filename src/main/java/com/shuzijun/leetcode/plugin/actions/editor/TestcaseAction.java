package com.shuzijun.leetcode.plugin.actions.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.shuzijun.leetcode.plugin.actions.AbstractAction;
import com.shuzijun.leetcode.plugin.manager.CodeManager;
import com.shuzijun.leetcode.plugin.manager.ViewManager;
import com.shuzijun.leetcode.plugin.model.CodeTypeEnum;
import com.shuzijun.leetcode.plugin.model.Config;
import com.shuzijun.leetcode.plugin.model.LeetcodeEditor;
import com.shuzijun.leetcode.plugin.model.Question;
import com.shuzijun.leetcode.plugin.setting.PersistentConfig;
import com.shuzijun.leetcode.plugin.setting.ProjectConfig;
import com.shuzijun.leetcode.plugin.utils.MessageUtils;
import com.shuzijun.leetcode.plugin.utils.PropertiesUtils;
import com.shuzijun.leetcode.plugin.window.TestcasePanel;
import org.apache.commons.lang.StringUtils;

/**
 * @author shuzijun
 */
public class TestcaseAction extends AbstractAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent, Config config) {

        VirtualFile vf = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        LeetcodeEditor leetcodeEditor = ProjectConfig.getInstance(anActionEvent.getProject()).getEditor(vf.getPath());
        if (leetcodeEditor == null) {
            return;
        }
        Question question = ViewManager.getQuestionById(leetcodeEditor.getQuestionId(), anActionEvent.getProject());
        if (question == null) {
            MessageUtils.getInstance(anActionEvent.getProject()).showInfoMsg("info", PropertiesUtils.getInfo("tree.null"));
            return;
        }
        if (StringUtils.isBlank(question.getTestCase())) {
            String codeType = PersistentConfig.getInstance().getInitConfig().getCodeType();
            CodeTypeEnum codeTypeEnum = CodeTypeEnum.getCodeTypeEnum(codeType);

            CodeManager.setTestCaeAndLang(question, codeTypeEnum, anActionEvent.getProject());
        }
        TestcasePanel dialog = new TestcasePanel(anActionEvent.getProject());
        dialog.setTitle(question.getFormTitle() + " Testcase");
        dialog.setText(question.getTestCase());
        if (dialog.showAndGet()) {
            String text = dialog.testcaeText();
            if (StringUtils.isBlank(text)) {
                MessageUtils.getInstance(anActionEvent.getProject()).showWarnMsg("info", PropertiesUtils.getInfo("test.case"));
                return;
            } else {

                question.setTestCase(text);
                CodeManager.RuncodeCode(question, anActionEvent.getProject());
            }


        }
    }

}
