package org.intellij.trinkets.eclipseMode.incrementalCompiler;

import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.SwingUtilities;

import org.intellij.trinkets.eclipseMode.EclipseMode;

import com.intellij.compiler.impl.ModuleCompileScope;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;

/**
 * VFS listener to handle save file event.
 *
 * @author Alexey Efimov
 */
public class SaveListener extends VirtualFileAdapter {
    @Override
    public void contentsChanged(VirtualFileEvent event) {
        executeMake(event);
    }

    @Override
    public void fileCreated(VirtualFileEvent event) {
        executeMake(event);
    }

    @Override
    public void fileDeleted(VirtualFileEvent event) {
        executeMake(event);
    }

    @Override
    public void fileMoved(VirtualFileMoveEvent event) {
        executeMake(event);
    }

    @Override
    public void fileCopied(VirtualFileCopyEvent event) {
        executeMake(event);
    }

    int i = 0;

    {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addVetoableChangeListener(new VetoableChangeListener() {
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                if (compiling) {
                    if (evt.getNewValue() != null) {
                        final Editor editor = DataKeys.EDITOR_EVEN_IF_INACTIVE.getData(DataManager.getInstance().getDataContext());
                        System.out.println("===============================FOCUS[" + i++ + "]==============================================");
                        System.out.println("editor:" + editor);
                        System.out.println("por:" + evt.getPropertyName());
                        System.out.println("old:" + evt.getOldValue());
                        System.out.println("new:" + evt.getNewValue());

                        if (evt.getPropertyName().equals("focusOwner") && evt.getNewValue().toString().startsWith("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
                            throw new PropertyVetoException("", evt);
                        }
                    }
                }
            }
        });
    }

    final java.util.Timer timer = new java.util.Timer();
    volatile boolean compiling = false;
    volatile boolean gotCaret = false;
    final static int AFTER_COMPILE_DELAY = 500;

    private void executeMake(VirtualFileEvent event) {
        final Editor editor = DataKeys.EDITOR_EVEN_IF_INACTIVE.getData(DataManager.getInstance().getDataContext());
        if (editor != null) {
            editor.getCaretModel().addCaretListener(new CaretListener() {
                @Override
                public void caretPositionChanged(CaretEvent e) {
                    if (!gotCaret && compiling) {
                        System.out.println("Scanning for Error Window");
                        try {
                            throw new RuntimeException();
                        } catch (RuntimeException ex) {
                            boolean errorPanelBadness = false;
                            for (StackTraceElement elem : ex.getStackTrace()) {
                                if (elem.getClassName().equals("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
                                    errorPanelBadness = true;
                                    break;
                                }
                            }
                            if (errorPanelBadness) {
                                gotCaret = true;
                                System.out.println("GOTCHA2");
                                e.getEditor().getCaretModel().moveToLogicalPosition(e.getOldPosition());
                                e.getEditor().getCaretModel().removeCaretListener(this);
                            }
                        }
                    }
                }

                @Override
                public void caretAdded(CaretEvent caretEvent) {

                }

                @Override
                public void caretRemoved(CaretEvent caretEvent) {

                }
            });
        }

        EclipseMode eclipseMode = EclipseMode.getInstance();
        if (event.isFromSave() && eclipseMode.getSettings().INCREMENTAL_COMPILATION_ENABLED) {
            Project[] projects = ProjectManager.getInstance().getOpenProjects();
            for (final Project project : projects) {
                if (project.isInitialized() && !project.isDisposed() &&
                      project.isOpen() && !project.isDefault()) {
                    ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
                    final Module module = projectFileIndex.getModuleForFile(event.getFile());
                    if (module != null) {
                        final CompilerManager compilerManager = CompilerManager.getInstance(project);
                        if (!compilerManager.isCompilationActive() &&
                              !compilerManager.isExcludedFromCompilation(event.getFile()) &&
                              !compilerManager.isUpToDate(new ModuleCompileScope(module, false))) {
                            System.out.println("<=====================COMPILE STARTED================================>");
                            compiling = true;
                            gotCaret = false;
                            compilerManager.make(module, new CompileStatusNotification() {
                                @Override
                                public void finished(boolean aborted, int errors, int warnings, CompileContext compileContext) {

                                    timer.schedule(new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            compiling = false;
                                        }
                                    }, AFTER_COMPILE_DELAY);
                                    final Editor editor = DataKeys.EDITOR_EVEN_IF_INACTIVE.getData(DataManager.getInstance().getDataContext());
                                    if (editor != null) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
//                                                System.out.println("AFTER focus request" + editor.getContentComponent());
//                                                editor.getContentComponent().requestFocusInWindow();
                                            }
                                        });

                                        editor.getCaretModel().addCaretListener(new CaretListener() {
                                            @Override
                                            public void caretPositionChanged(CaretEvent e) {
                                                if (!gotCaret && compiling) {
                                                    System.out.println("Scanning for Error Window");
                                                    try {
                                                        throw new RuntimeException();
                                                    } catch (RuntimeException ex) {
                                                        boolean errorPanelBadness = false;
                                                        for (StackTraceElement elem : ex.getStackTrace()) {
                                                            if (elem.getClassName().equals("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
                                                                errorPanelBadness = true;
                                                                break;
                                                            }
                                                        }
                                                        if (errorPanelBadness) {
                                                            gotCaret = true;
                                                            System.out.println("GOTCHA3");
                                                            e.getEditor().getCaretModel().moveToLogicalPosition(e.getOldPosition());
                                                            e.getEditor().getCaretModel().removeCaretListener(this);
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void caretAdded(CaretEvent caretEvent) {

                                            }

                                            @Override
                                            public void caretRemoved(CaretEvent caretEvent) {

                                            }
                                        });
                                    } else {
                                        System.out.println("NO EDITOR");
                                    }
                                    System.out.println("<=====================COMPILE ENDED================================>");
//                                    System.out.println("AFTER");
//                                    System.out.println("focus owner->" + KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getClass());
//                                    System.out.println("focus window->" + KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow().getClass());
//                                    System.out.println("focus active->" + KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow().getClass());
//                                    System.out.println("focus perm->" + KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner().getClass());
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
