package org.intellij.trinkets.eclipseMode.incrementalCompiler;

import com.intellij.compiler.impl.ModuleCompileScope;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.ide.DataManager;
import com.intellij.pom.Navigatable;
import org.intellij.trinkets.eclipseMode.EclipseMode;

import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * VFS listener to handle save file event.
 *
 * @author Alexey Efimov
 */
public class SaveListener extends VirtualFileAdapter {
    int i = 0;

    long lastCompileTime = 0;
    boolean blockPopup = false;
    enum BlockPopUpState {
        NONE,
        ERROR1,
        ERROR2,
        EDITOR1,
        EDITOR2
    }

    {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addVetoableChangeListener(new VetoableChangeListener() {
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                EclipseMode eclipseMode = EclipseMode.getInstance();
                System.out.println("dT:" + (System.currentTimeMillis() - lastCompileTime));
                if (System.currentTimeMillis() - lastCompileTime <= 2000) {
                    System.out.println("=====================FOCUS EVENT[" + i++  +"]=====================");
                    System.out.println(evt.getPropertyName());
                    System.out.println("old:"+evt.getOldValue());
                    System.out.println("new:"+evt.getNewValue());
                    System.out.println("src:"+evt.getSource());
                    System.out.println("pid:"+evt.getPropagationId());
                    if (blockPopup && eclipseMode.getSettings().PREVENT_ERROR_TREE_FOCUS_GRAB && evt.getPropertyName().equals("focusOwner") && (""+evt.getNewValue()).startsWith("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
                        System.out.println("VETOED!" +(evt.getNewValue().getClass()) );
                        //blockPopup = false;
                        throw new PropertyVetoException("", evt);
                    }
                    if (blockPopup && eclipseMode.getSettings().PREVENT_ERROR_TREE_FOCUS_GRAB && evt.getPropertyName().equals("permanentFocusOwner") && (""+evt.getNewValue()).toString().startsWith("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
                        System.out.println("VETOED!" +(evt.getNewValue().getClass()));
                        //blockPopup = false;
                        throw new PropertyVetoException("", evt);
                    }
                } else {
                    blockPopup = false;
                }

//                EclipseMode eclipseMode = EclipseMode.getInstance();
////                System.out.println("=====================FOCUS EVENT[" + i++  +"]=====================");
//                System.out.println(evt.getPropertyName());
////                System.out.println("old:"+evt.getOldValue());
//                System.out.println("new:"+evt.getNewValue());
////                System.out.println("src:"+evt.getSource());
////                System.out.println("pid:"+evt.getPropagationId());
//                if (blockPopup && eclipseMode.getSettings().PREVENT_ERROR_TREE_FOCUS_GRAB && evt.getPropertyName().equals("focusOwner") && (""+evt.getNewValue()).startsWith("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
//                    System.out.println("VETOED!" +(evt.getNewValue().getClass()) );
////                    oldEditor.getCaretModel().moveToOffset(offset);
////                    oldEditor = null;
//                    blockPopup = false;
//                    throw new PropertyVetoException("", evt);
//                }
//                if (blockPopup && eclipseMode.getSettings().PREVENT_ERROR_TREE_FOCUS_GRAB && evt.getPropertyName().equals("permanentFocusOwner") && (""+evt.getNewValue()).toString().startsWith("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
//                    System.out.println("VETOED!" +(evt.getNewValue().getClass()));
////                    oldEditor.getCaretModel().moveToOffset(offset);
////                    oldEditor = null;
//                    blockPopup = false;
//                    throw new PropertyVetoException("", evt);
//                }
////                if (eclipseMode.getSettings().PREVENT_ERROR_TREE_FOCUS_GRAB && blockEditor && evt.getPropertyName().equals("focusOwner") && (""+evt.getNewValue()).toString().startsWith("com.intellij.openapi.editor.impl.EditorComponentImpl")) {
////                    System.out.println("VETOED!" +(evt.getNewValue().getClass()));
////                    blockEditor = false;
////                    throw new PropertyVetoException("", evt);
////                }
////                if (eclipseMode.getSettings().PREVENT_ERROR_TREE_FOCUS_GRAB && blockEditor && evt.getPropertyName().equals("permanentFocusOwner") && (""+evt.getNewValue()).toString().startsWith("com.intellij.openapi.editor.impl.EditorComponentImpl")) {
////                    System.out.println("VETOED!" +(evt.getNewValue().getClass()));
////                    blockEditor = false;
////                    throw new PropertyVetoException("", evt);
////                }
            }
        });
    }

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

//    {
//
//        KeyboardFocusManager.getCurrentKeyboardFocusManager().addVetoableChangeListener(new VetoableChangeListener() {
//            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
//                EclipseMode eclipseMode = EclipseMode.getInstance();
//
//                if (eclipseMode.getSettings().PREVENT_ERROR_TREE_FOCUS_GRAB && evt.getPropertyName().equals("focusOwner") && ("" + evt.getNewValue()).startsWith("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
//                    throw new PropertyVetoException("", evt);
//                }
//                if (eclipseMode.getSettings().PREVENT_ERROR_TREE_FOCUS_GRAB && evt.getPropertyName().equals("permanentFocusOwner") && ("" + evt.getNewValue()).toString().startsWith("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
//                    throw new PropertyVetoException("", evt);
//                }
//            }
//        });
//
//    }

    boolean addEd = true;
    boolean addCo = true;
    boolean catchCaret = false;
    int offset = -1;
    Editor oldEditor = null;

    private void executeMake(VirtualFileEvent event) {
        EclipseMode eclipseMode = EclipseMode.getInstance();

        if (addEd) {
            addEd = false;
            Editor editor = DataKeys.EDITOR.getData(DataManager.getInstance().getDataContext());
//            editor.getCaretModel().addCaretListener(new CaretListener() {
//                @Override
//                public void caretPositionChanged(CaretEvent e) {
//                    if (catchCaret) {
//                        System.out.println("=====================CARET EVENT[" + i++  +"]=====================");
//                        System.out.println("old:"+e.getOldPosition());
//                        System.out.println("new:"+e.getNewPosition());
//                        System.out.println("src:"+e.getSource());
//                        System.out.println("==================================================================");
//                        try {
//                            throw new RuntimeException();
//                        } catch(RuntimeException ex) {
//                            boolean errorPanelBadness = false;
//                            for (StackTraceElement elem : ex.getStackTrace()) {
//                                if (elem.getClassName().equals("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
//                                    errorPanelBadness = true;
//                                    break;
//                                }
//                            }
//                            if (errorPanelBadness) {
//                                System.out.println("GOTCHA!");
//                            }
//                        }
//                        System.out.println("==================================================================");
//                    }
//                }
//            });
            editor.getCaretModel().addCaretListener(new CaretListener() {
                @Override
                public void caretPositionChanged(CaretEvent e) {
                    if (catchCaret) {
                        System.out.println("Scanning for Error Window");
                        try {
                            throw new RuntimeException();
                        } catch(RuntimeException ex) {
                            boolean errorPanelBadness = false;
                            for (StackTraceElement elem : ex.getStackTrace()) {
                                if (elem.getClassName().equals("com.intellij.ide.errorTreeView.NewErrorTreeViewPanel")) {
                                    errorPanelBadness = true;
                                    break;
                                }
                            }
                            if (errorPanelBadness) {
                                catchCaret = false;
                                e.getEditor().getCaretModel().moveToLogicalPosition(e.getOldPosition());
//                                e.getEditor().getCaretModel().removeCaretListener(this);
                            }
                        }
                    }
                }
            });


        }


        if (event.isFromSave() && eclipseMode.getSettings().INCREMENTAL_COMPILATION_ENABLED) {
            Project[] projects = ProjectManager.getInstance().getOpenProjects();
            for (final Project project : projects) {
                if (project.isInitialized() && !project.isDisposed() &&
                        project.isOpen() && !project.isDefault()) {
                    ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
                    final Module module = projectFileIndex.getModuleForFile(event.getFile());
                    if (module != null) {
                        CompilerManager compilerManager = CompilerManager.getInstance(project);
                        if (addCo) {
                            addCo = false;
                            compilerManager.addCompilationStatusListener(new CompilationStatusListener() {
                                @Override
                                public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                                    System.out.println();
                                    System.out.println("Compilation status");
                                    System.out.println("====================================");
                                    System.out.println("aborted:"+aborted);
                                    System.out.println("errors:"+errors);
                                    System.out.println("warnings:"+warnings);
                                    System.out.println("context:"+compileContext);
                                    lastCompileTime = System.currentTimeMillis();
                                    if (errors > 0) {
                                        catchCaret = true;
                                    }
                                    if (errors > 0 || warnings > 0) {
                                        blockPopup = true;
                                        //Editor editor = DataKeys.EDITOR.getData(DataManager.getInstance().getDataContext());
                                        //offset = oldEditor.getCaretModel().getOffset();
//                                        if (compileContext.getMessageCount(CompilerMessageCategory.ERROR) > 0) {
//                                            CompilerMessage msg = compileContext.getMessages(CompilerMessageCategory.ERROR)[0];
//                                            for (Field f : msg.getClass().getDeclaredFields()) {
//                                                try {
//                                                    f.setAccessible(true);
//                                                    System.out.println(f.getType() + "->" + f.get(msg));
//                                                } catch (Throwable t) {
//                                                    t.printStackTrace();
//                                                }
//
//                                                if (Navigatable.class.isAssignableFrom(f.getType())) {
//                                                    try {
////                                                        f.setAccessible(true);
////                                                        (Navigatable) f.get(msg);
//                                                    } catch (Throwable t) {
//                                                        t.printStackTrace();
//                                                    }
//                                                }
//                                            }
//                                        }
                                    }
                                }
                            });
                        }
                        if (!compilerManager.isCompilationActive() &&
                                !compilerManager.isExcludedFromCompilation(event.getFile()) &&
                                !compilerManager.isUpToDate(new ModuleCompileScope(module, false))) {
                            // Changed file found in module. Make it.
                            compilerManager.make(module, null);
                        }
                    }
                }
            }
        }
    }
}
