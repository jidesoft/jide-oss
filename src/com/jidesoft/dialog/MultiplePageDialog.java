/*
* @(#)MultiplePageDialog.java
*
* Copyright 2002 - 2003 JIDE Software. All rights reserved.
*/
package com.jidesoft.dialog;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideScrollPane;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * MultiplePageDialog is a StandardDialog which can have multiple AbstractDialogPages. You can choose one from four
 * predefined styles of how to change the page visibility. Those four styles are TAB_STYLE, ICON_STYLE, LIST_STYLE and
 * TREE_STYLE.
 * <p/>
 * To use this class, just create a PageList of AbstractDialogPage and call setPageList() to set to this dialog. Based
 * on the style, the class will automatically layout those pages correctly and hook up actions to switch based on user
 * selection.
 * <p/>
 * As AbstractDialogPage extends AbstractPage, so you can always use PageListener to decide what to do when page is
 * opened, closing, closed, activated or deactivated.
 * <p/>
 * We automatically create a button panel which have three button - OK, Cancel and Apply. The ButtonPanel listens to
 * ButtonEvent from all the pages. You can simply fireButtonEvent in the page to change the state of those buttons. Or
 * if you want to create your own button panel, just override createButtonPanel() method.
 * <p/>
 * If you choose LIST_STYLE and TREE_STYLE, you can set your own ListCellRenderer and TreeCellRenderer. Just call
 * setListCellRenderer() and setTreeCellRenderer(). The value passed in the renderer is an instance of
 * AbstractDialogPage associated with that list row or tree node.
 */
public class MultiplePageDialog extends StandardDialog {
    /**
     * Predefined style of multiple page dialog.
     */
    public static final int TAB_STYLE = 0;

    /**
     * Predefined style of multiple page dialog.
     */
    public static final int TREE_STYLE = 1;

    /**
     * Predefined style of multiple page dialog.
     */
    public static final int LIST_STYLE = 2;

    /**
     * Predefined style of multiple page dialog.
     */
    public static final int ICON_STYLE = 3;

    private int _style;

    private PageList _pageList;

    /**
     * The left pane to show the icon, list etc. It's an index area to choose which page.
     */
    private JComponent _indexPanel;

    /**
     * The panel contains all the pages. In TAB_STYLE, it is a tabbed pane and in other styles, it's a panel with
     * CardLayout.
     */
    private JComponent _pagesPanel;
    private CardLayout _cardLayout;

    /**
     * Map that maps from page full title to tree node. It provides a fast access from page full title to the tree node
     * in TREE_STYLE.
     */
    private Map _titleNodeMap;

    private JButton _okButton;
    private JButton _cancelButton;
    private JButton _applyButton;

    private TreeCellRenderer _treeCellRenderer;

    private ListCellRenderer _listCellRenderer;
    private JTabbedPane _tabbedPane;

    private String _initialPageTitle;
    public JTree _tree;

    /**
     * Creates a non-modal MultiplePageDialog without a title and without a specified <code>Frame</code> owner.  A
     * shared, hidden frame will be set as the owner of the dialog. By default TAB_STYLE is used.
     *
     * @throws HeadlessException
     */
    public MultiplePageDialog() throws HeadlessException {
        this((Frame) null);
    }

    /**
     * Creates a non-modal MultiplePageDialog without a title with the specified <code>Frame</code> as its owner.  If
     * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog. By
     * default TAB_STYLE is used.
     *
     * @param owner
     * @throws HeadlessException
     */
    public MultiplePageDialog(Frame owner) throws HeadlessException {
        this(owner, false);
    }

    /**
     * Creates a modal or non-modal MultiplePageDialog without a title and with the specified owner <code>Frame</code>.
     * If <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog. By
     * default TAB_STYLE is used.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param modal true for a modal dialog, false for one that allows others windows to be active at the same time
     * @throws HeadlessException
     */
    public MultiplePageDialog(Frame owner, boolean modal) throws HeadlessException {
        this(owner, "", modal);
    }

    /**
     * Creates a non-modal MultiplePageDialog with the specified title and with the specified owner frame.  If
     * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title the <code>String</code> to display in the dialog's title bar
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public MultiplePageDialog(Frame owner, String title) throws HeadlessException {
        this(owner, title, true);
    }

    /**
     * Creates a modal or non-modal dialog with the specified title and the specified owner <code>Frame</code>.  If
     * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of this dialog.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title the <code>String</code> to display in the dialog's title bar
     * @param modal true for a modal dialog, false for one that allows other windows to be active at the same time
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public MultiplePageDialog(Frame owner, String title, boolean modal) throws HeadlessException {
        this(owner, title, modal, TAB_STYLE);
    }

    /**
     * Creates a modal or non-modal MultiplePageDialog with the specified style, the specified title and the specified
     * owner <code>Frame</code>.  If <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the
     * owner of this dialog.  All constructors defer to this one.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title the <code>String</code> to display in the dialog's title bar
     * @param modal true for a modal dialog, false for one that allows other windows to be active at the same time
     * @param style the style. It must be one of the following: TAB_STYLE, ICON_STYLE, LIST_STYLE or TREE_STYLE.
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public MultiplePageDialog(Frame owner, String title, boolean modal, int style) throws HeadlessException {
        super(owner, title, modal);
        setStyle(style);
    }

    /**
     * Creates a non-modal MultiplePageDialog without a title with the specified <code>Dialog</code> as its owner.  If
     * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog. By
     * default TAB_STYLE is used.
     *
     * @param owner
     * @throws HeadlessException
     */
    public MultiplePageDialog(Dialog owner) throws HeadlessException {
        this(owner, false);
    }

    /**
     * Creates a modal or non-modal MultiplePageDialog without a title and with the specified owner <code>Dialog</code>.
     * If <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog. By
     * default TAB_STYLE is used.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param modal true for a modal dialog, false for one that allows others windows to be active at the same time
     * @throws HeadlessException
     */
    public MultiplePageDialog(Dialog owner, boolean modal) throws HeadlessException {
        this(owner, "", modal);
    }

    /**
     * Creates a non-modal MultiplePageDialog with the specified title and with the specified owner frame.  If
     * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
     *
     * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param title the <code>String</code> to display in the dialog's title bar
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public MultiplePageDialog(Dialog owner, String title) throws HeadlessException {
        this(owner, title, true);
    }

    /**
     * Creates a modal or non-modal dialog with the specified title and the specified owner <code>Dialog</code>.  If
     * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of this dialog.
     *
     * @param owner the <code>Dialog</code> from which the dialog is displayed
     * @param title the <code>String</code> to display in the dialog's title bar
     * @param modal true for a modal dialog, false for one that allows other windows to be active at the same time
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public MultiplePageDialog(Dialog owner, String title, boolean modal) throws HeadlessException {
        this(owner, title, modal, TAB_STYLE);
    }

    /**
     * Creates a modal or non-modal MultiplePageDialog with the specified style, the specified title and the specified
     * owner <code>Dialog</code>.  If <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the
     * owner of this dialog.  All constructors defer to this one.
     *
     * @param owner the <code>Dialog</code> from which the dialog is displayed
     * @param title the <code>String</code> to display in the dialog's title bar
     * @param modal true for a modal dialog, false for one that allows other windows to be active at the same time
     * @param style the style. It must be one of the following: TAB_STYLE, ICON_STYLE, LIST_STYLE or TREE_STYLE.
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public MultiplePageDialog(Dialog owner, String title, boolean modal, int style) throws HeadlessException {
        super(owner, title, modal);
        setStyle(style);
    }

    /**
     * Implements the method in StandardDialog. You can override this method to create a BannerPanel.
     *
     * @return the BannerPanel
     */
    @Override
    public JComponent createBannerPanel() {
        return null;
    }

    /**
     * Implements the method in StandardDialog. You can override this method to create a ContentPanel. By default, a
     * JPanel with BorderLayout is created. IndexPanel is added to WEST and PagesPanel is added to CENTER.
     *
     * @return the ContentPanel
     */
    @Override
    public JComponent createContentPanel() {
        _indexPanel = createIndexPanel();
        _pagesPanel = createPagesPanel();
        if (_pageList.getPageCount() > 0) {
            if (getInitialPageTitle() != null) {
                setCurrentPage(getInitialPageTitle());
            }
            else {
                setCurrentPage(_pageList.getPage(0));
            }
        }
        return setupContentPanel(_indexPanel, _pagesPanel);
    }

    /**
     * Setups the content panel. It will use the index panel and the pages panel created earlier and put it into another
     * panel.
     *
     * @param indexPanel the index panel. It has the nagivation control to control which page to show.
     * @param pagesPanel the pages panel. It contains all the pages of this dialog.
     * @return the panel that contains both index panel and pages panel.
     */
    protected JComponent setupContentPanel(JComponent indexPanel, JComponent pagesPanel) {
        JPanel middlePanel = new JPanel(new BorderLayout(10, 10));
        if (indexPanel != null) {
            middlePanel.add(indexPanel, BorderLayout.BEFORE_LINE_BEGINS);
        }
        if (pagesPanel != null) {
            middlePanel.add(pagesPanel, BorderLayout.CENTER);
        }
        return middlePanel;
    }

    /**
     * Creates the button panel. It has three buttons - OK, Cancel and Apply. If you want to create your own button
     * panel, just override this method.
     *
     * @return button panel
     */
    @Override
    public ButtonPanel createButtonPanel() {
        ButtonPanel buttonPanel = new ButtonPanel();
        _okButton = new JButton();
        _cancelButton = new JButton();
        _applyButton = new JButton();
        _okButton.setName(OK);
        _cancelButton.setName(CANCEL);
        _applyButton.setName(APPLY);
        buttonPanel.addButton(_okButton, ButtonPanel.AFFIRMATIVE_BUTTON);
        buttonPanel.addButton(_cancelButton, ButtonPanel.CANCEL_BUTTON);
        buttonPanel.addButton(_applyButton, ButtonPanel.OTHER_BUTTON);

        Locale l = getLocale();
        _okButton.setAction(new AbstractAction(UIDefaultsLookup.getString("OptionPane.okButtonText", l)) {
            public void actionPerformed(ActionEvent e) {
                setDialogResult(RESULT_AFFIRMED);
                setVisible(false);
                dispose();
            }
        });
        _cancelButton.setAction(new AbstractAction(UIDefaultsLookup.getString("OptionPane.cancelButtonText", l)) {
            public void actionPerformed(ActionEvent e) {
                setDialogResult(RESULT_CANCELLED);
                setVisible(false);
                dispose();
            }
        });
        _applyButton.setAction(new AbstractAction(ButtonResources.getResourceBundle(Locale.getDefault()).getString("Button.apply")) {
            public void actionPerformed(ActionEvent e) {
                if (getCurrentPage() != null) {
                    getCurrentPage().fireButtonEvent(ButtonEvent.DISABLE_BUTTON, APPLY);
                }
            }
        });
        _applyButton.setMnemonic(ButtonResources.getResourceBundle(Locale.getDefault()).getString("Button.apply.mnemonic").charAt(0));
        _applyButton.setEnabled(false);

        setDefaultCancelAction(_cancelButton.getAction());
        setDefaultAction(_okButton.getAction());
        getRootPane().setDefaultButton(_okButton);
        return buttonPanel;
    }

    /**
     * Gets the OK Button only if you didn't override the createButtonPanel() and remove the OK button.
     *
     * @return the OK Button
     */
    public JButton getOkButton() {
        return _okButton;
    }

    /**
     * Gets the cancel button. only if you didn't override the createButtonPanel() and remove the cancel button.
     *
     * @return the cancel button.
     */
    public JButton getCancelButton() {
        return _cancelButton;
    }

    /**
     * Gets the apply button. only if you didn't override the createButtonPanel() and remove the apply button.
     *
     * @return the apply button.
     */
    public JButton getApplyButton() {
        return _applyButton;
    }

    /**
     * Creates the pages panel. If it's TAB_STYLE, a tabbed pane will be created. If it's any other styles, a JPanel
     * with CardLayout will be created.
     *
     * @return a panel containing all the pages.
     */
    protected JComponent createPagesPanel() {
        if (_style == TAB_STYLE) {
            _tabbedPane = createTabbedPane();
            _tabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    Component selectedComponent = _tabbedPane.getSelectedComponent();
                    if (selectedComponent instanceof AbstractDialogPage) {
                        setCurrentPage((AbstractDialogPage) selectedComponent, _tabbedPane);
                    }
                }
            });
            for (int i = 0; i < _pageList.getPageCount(); i++) {
                AbstractDialogPage page = _pageList.getPage(i);
                page.addButtonListener(getButtonPanel());
                _tabbedPane.addTab(page.getTitle(), page.getIcon(), page, page.getDescription());
                _tabbedPane.setEnabledAt(i, page.isPageEnabled());
                final int index = i;
                page.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (AbstractDialogPage.PROPERTY_PAGE_ENABLED.equals(evt.getPropertyName())) {
                            _tabbedPane.setEnabledAt(index, Boolean.TRUE.equals(evt.getNewValue()));
                        }
                        else if (AbstractDialogPage.ICON_PROPERTY.equals(evt.getPropertyName())) {
                            _tabbedPane.setIconAt(index, (Icon) evt.getNewValue());
                        }
                        else if (AbstractDialogPage.TITLE_PROPERTY.equals(evt.getPropertyName())) {
                            _tabbedPane.setTitleAt(index, (String) evt.getNewValue());
                        }
                        else if (AbstractDialogPage.DESCRIPTION_PROPERTY.equals(evt.getPropertyName())) {
                            _tabbedPane.setToolTipTextAt(index, (String) evt.getNewValue());
                        }
                    }
                });
            }
            _pageList.addListDataListener(new ListDataListener() {
                public void intervalAdded(ListDataEvent e) {
                    for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                        AbstractDialogPage page = _pageList.getPage(i);
                        _tabbedPane.insertTab(page.getTitle(), page.getIcon(), page, page.getDescription(), i);
                    }
                }

                public void intervalRemoved(ListDataEvent e) {
                    for (int i = e.getIndex1(); i >= e.getIndex0(); i--) {
                        _tabbedPane.removeTabAt(i);
                    }
                }

                public void contentsChanged(ListDataEvent e) {
                }
            });
            return _tabbedPane;
        }
        else {
            final JPanel pagesPanel = new JPanel();
            _cardLayout = new CardLayout();
            pagesPanel.setLayout(_cardLayout);

            for (int i = 0; i < _pageList.getPageCount(); i++) {
                AbstractDialogPage page = _pageList.getPage(i);
                page.addButtonListener(getButtonPanel());
                page.setName(page.getFullTitle());
                page.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (AbstractDialogPage.TITLE_PROPERTY.equals(evt.getPropertyName())) {
                            for (int j = 0; j < pagesPanel.getComponentCount(); j++) {
                                Component c = pagesPanel.getComponent(j);
                                boolean wasVisible = c.isVisible();
                                Object source = evt.getSource();
                                if (source instanceof AbstractDialogPage && c == source) {
                                    pagesPanel.remove(j);
                                    String fullTitle = ((AbstractDialogPage) source).getFullTitle();
                                    pagesPanel.add((AbstractDialogPage) source, fullTitle, j);
                                    ((AbstractDialogPage) source).setName(fullTitle);
                                    getIndexPanel().repaint();
                                    if (wasVisible) {
                                        _cardLayout.show(pagesPanel, fullTitle);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });

                pagesPanel.add(page, page.getFullTitle());
            }

            _pageList.addListDataListener(new ListDataListener() {
                public void intervalAdded(ListDataEvent e) {
                    for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                        AbstractDialogPage page = _pageList.getPage(i);
                        page.setName(page.getFullTitle());
                        pagesPanel.add(page, page.getFullTitle(), i);
                    }
                }

                public void intervalRemoved(ListDataEvent e) {
                    for (int i = e.getIndex1(); i >= e.getIndex0(); i--) {
                        pagesPanel.remove(i);
                    }
                }

                private void dumpPagesPanel() {
                    for (int i = 0; i < pagesPanel.getComponentCount(); i++) {
                        System.out.println("" + i + ": " + pagesPanel.getComponent(i).getName());
                    }
                }

                public void contentsChanged(ListDataEvent e) {
                    if (e.getSource() instanceof PageList) {
                        Object o = ((PageList) e.getSource()).getSelectedItem();
                        if (o instanceof AbstractDialogPage) {
                            setCurrentPage((AbstractDialogPage) o);
                        }
                    }
                }
            });
            return pagesPanel;
        }
    }

    /**
     * Creates the JTabbedPane used by TAB_STYLE dialog.
     *
     * @return a JTabbedPane
     */
    protected JTabbedPane createTabbedPane() {
        return new JTabbedPane(JTabbedPane.TOP);
    }

    /**
     * Creates the index panel based on the style.
     *
     * @return the index panel.
     */
    public JComponent createIndexPanel() {
        switch (_style) {
            case ICON_STYLE:
                return createIconPanel();
            case LIST_STYLE:
                return createListPanel();
            case TREE_STYLE:
                return createTreePanel();
            case TAB_STYLE:
            default:
                return null;
        }
    }

    /**
     * Sets the page list of this dialog. User must call this method before the dialog is set visible.
     *
     * @param pageList
     */
    public void setPageList(PageList pageList) {
        _pageList = pageList;
    }

    /**
     * Gets the page list of this dialog.
     */
    public PageList getPageList() {
        return _pageList;
    }

    /**
     * Gets the current selected page.
     *
     * @return the current selected page.
     */
    public AbstractDialogPage getCurrentPage() {
        return _pageList.getCurrentPage();
    }

    protected void setCurrentPage(String pageTitle) {
        if (_pageList != null) {
            setCurrentPage(_pageList.getPageByFullTitle(pageTitle));
        }
    }

    protected void setCurrentPage(AbstractDialogPage currentPage) {
        setCurrentPage(currentPage, null);
    }

    protected void setCurrentPage(AbstractDialogPage currentPage, Object source) {
        if (!_pageList.setCurrentPage(currentPage, source)) {
            return;
        }

        if (currentPage != null) {
            showCurrentPage(currentPage);
        }
    }

    /**
     * Displays the current page. If it is TAB_STYLE, this method will simply select the tab that has the current page.
     * If it is any of the other styles, this method will show the page that is already added in a CardLayout in
     * createPagePanel method.
     *
     * @param currentPage
     */
    protected void showCurrentPage(AbstractDialogPage currentPage) {
        if (currentPage != null) {
            if (getStyle() == TAB_STYLE) {
                _tabbedPane.setSelectedComponent(currentPage);
            }
            else {
                _cardLayout.show(_pagesPanel, currentPage.getFullTitle());
            }
            currentPage.focusDefaultFocusComponent();
        }
    }

    private JComponent createTreePanel() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("", true);

        _titleNodeMap = new HashMap((int) (_pageList.getPageCount() * 0.75));
        for (int i = 0; i < _pageList.getPageCount(); i++) {
            AbstractDialogPage dialogPage = _pageList.getPage(i);
            addPage(dialogPage, root, false);
        }

        _tree = createTree(root);
        configureTree(_tree);
        _pageList.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
                for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                    AbstractDialogPage dialogPage = _pageList.getPage(i);
                    addPage(dialogPage, (DefaultMutableTreeNode) _tree.getModel().getRoot(), true);
                }
            }

            public void intervalRemoved(ListDataEvent e) {
                // compare PageList with TitleNodeMap to find out what is missing
                Set set = _titleNodeMap.keySet();
                Vector toBeRemoved = new Vector();
                for (Object o : set) {
                    String title = (String) o;
                    if (_pageList.getPageByFullTitle(title) == null) {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) _titleNodeMap.get(title);
                        if (treeNode != null) {
                            toBeRemoved.add(title);
                            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treeNode.getParent();
                            if (parentNode != null) {
                                int index = parentNode.getIndex(treeNode);
                                parentNode.remove(treeNode);
                                ((DefaultTreeModel) _tree.getModel()).nodesWereRemoved(parentNode, new int[]{index}, new Object[]{treeNode});
                            }
                        }
                    }
                }
                for (Object o : toBeRemoved) {
                    _titleNodeMap.remove(o);
                }
            }

            public void contentsChanged(ListDataEvent e) {
                if (e.getIndex0() == -1 && e.getIndex1() == -1 && e.getType() == ListDataEvent.CONTENTS_CHANGED) {
                    if (_titleNodeMap != null && _pageList.getCurrentPage() != null) {
                        TreeNode node = (TreeNode) _titleNodeMap.get(_pageList.getCurrentPage().getFullTitle());
                        if (node != null) {
                            ArrayList list = new ArrayList();
                            while (node != null) {
                                list.add(0, node);
                                node = node.getParent();
                            }
                            TreePath treePath = new TreePath(list.toArray(new TreeNode[list.size()]));
                            _tree.getSelectionModel().setSelectionPath(treePath);
                        }
                    }
                }
            }
        });

        JComponent indexPanel = new JPanel(new BorderLayout());
        indexPanel.add(new JScrollPane(_tree), BorderLayout.CENTER);
        return indexPanel;
    }

    /**
     * Creates tree that is used in TREE_STYLE dialog's index panel. Below is the code we used. If you just want to have
     * a different cell renderer, you can just call {@link #setTreeCellRenderer(javax.swing.tree.TreeCellRenderer)} to
     * set a new one.
     * <pre><code>
     * UIManager.put("Tree.hash", Color.white);
     * return new JTree(root);
     * </code></pre>
     *
     * @param root
     * @return tree
     */
    protected JTree createTree(DefaultMutableTreeNode root) {
        UIManager.put("Tree.hash", Color.white);
        return new JTree(root);
    }

    /**
     * Configure the JTree used in TREE_STYLE dialog. Subclass can override this method to configure the JTree to the
     * way you want. Below is the default implementation of this method.
     * <code><pre>
     * tree.setToggleClickCount(1);
     * tree.setCellRenderer(createTreeCellRenderer());
     * tree.setRootVisible(false);
     * tree.setShowsRootHandles(false);
     * tree.addTreeSelectionListener(new TreeSelectionListener() {
     *     public void valueChanged(TreeSelectionEvent e) {
     *         if (tree.getSelectionPath() == null) {
     *             return;
     *         }
     * <p/>
     *         DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)
     * tree.getSelectionPath().getLastPathComponent();
     *         // comment this while block if you want the parent page shows its own page instead
     * of
     * showing its first child page.
     *         while (!treeNode.isLeaf()) {
     *             final DefaultMutableTreeNode tn = treeNode;
     *             Runnable runnable = new Runnable() {
     *                 public void run() {
     *                     tree.expandPath(new TreePath(tn.getPath()));
     *                 }
     *             };
     *             SwingUtilities.invokeLater(runnable);
     *             treeNode = (DefaultMutableTreeNode) treeNode.getChildAt(0);
     *         }
     * <p/>
     *         if (treeNode != null) {
     *             Object userObject = treeNode.getUserObject();
     *             if (userObject instanceof AbstractDialogPage) {
     *                 setCurrentPage((AbstractDialogPage) userObject, tree);
     *             }
     *         }
     *     }
     * });
     * </pre></code>
     *
     * @param tree
     */
    protected void configureTree(final JTree tree) {
        tree.setToggleClickCount(1);
        tree.setCellRenderer(createTreeCellRenderer());
        tree.setRootVisible(false);
        tree.setShowsRootHandles(false);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (tree.getSelectionPath() == null) {
                    return;
                }

                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

                // comment this while block if you want the parent page shows its own page instead of showing its first child page.
                while (!treeNode.isLeaf()) {
                    final DefaultMutableTreeNode tn = treeNode;
                    Runnable runnable = new Runnable() {
                        public void run() {
                            tree.expandPath(new TreePath(tn.getPath()));
                        }
                    };
                    SwingUtilities.invokeLater(runnable);
                    treeNode = (DefaultMutableTreeNode) treeNode.getChildAt(0);
                }

                Object userObject = treeNode.getUserObject();
                if (userObject instanceof AbstractDialogPage && !userObject.equals(getCurrentPage())) {
                    setCurrentPage((AbstractDialogPage) userObject, tree);
                    if (getCurrentPage() != userObject) {
                        // TODO select the old path.
                    }
                }
            }
        });
    }

    private void addPage(AbstractDialogPage dialogPage, final DefaultMutableTreeNode root, boolean fireEvent) {
        if (dialogPage == null) {
            return;
        }

        if (dialogPage.getParentPage() == null) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(dialogPage);
            _titleNodeMap.put(dialogPage.getFullTitle(), treeNode);
            root.add(treeNode);
            if (fireEvent) {
                ((DefaultTreeModel) _tree.getModel()).nodesWereInserted(root, new int[]{root.getIndex(treeNode)});
            }
        }
        else {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(dialogPage);
            _titleNodeMap.put(dialogPage.getFullTitle(), treeNode);
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) _titleNodeMap.get(dialogPage.getParentPage().getFullTitle());
            if (parentNode != null) {
                parentNode.add(treeNode);
                if (fireEvent) {
                    ((DefaultTreeModel) _tree.getModel()).nodesWereInserted(parentNode, new int[]{parentNode.getIndex(treeNode)});
                }
            }
        }
    }

    private void removePage(AbstractDialogPage dialogPage, final DefaultMutableTreeNode root, boolean fireEvent) {
        if (dialogPage == null) {
            return;
        }

        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) _titleNodeMap.get(dialogPage.getFullTitle());

        if (treeNode == null) {
            return;
        }

        if (treeNode.getChildCount() > 0) {
            throw new IllegalArgumentException("Please remove all children pages before removing parent page \"" + dialogPage.getFullTitle() + "\"");
        }
        _titleNodeMap.remove(dialogPage.getFullTitle());
        if (dialogPage.getParentPage() == null) {
            int index = root.getIndex(treeNode);
            root.remove(treeNode);
            if (fireEvent) {
                ((DefaultTreeModel) _tree.getModel()).nodesWereRemoved(root, new int[]{index}, new Object[]{treeNode});
            }
        }
        else {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) _titleNodeMap.get(dialogPage.getParentPage().getFullTitle());
            if (parentNode != null) {
                int index = parentNode.getIndex(treeNode);
                parentNode.remove(treeNode);
                if (fireEvent) {
                    ((DefaultTreeModel) _tree.getModel()).nodesWereRemoved(parentNode, new int[]{index}, new Object[]{treeNode});
                }
            }
        }
    }

    private JComponent createListPanel() {
        final DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < _pageList.getPageCount(); i++) {
            AbstractDialogPage optionsPanel = _pageList.getPage(i);
            listModel.addElement(optionsPanel);
        }

        final JList list = createList(listModel);
        if (list.getModel().getSize() > 0) {
            list.setSelectedIndex(0);
        }
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedValue() == getCurrentPage()) {
                    return;
                }
                if (!e.getValueIsAdjusting()) {
                    AbstractDialogPage page = (AbstractDialogPage) list.getSelectedValue();
                    if (page != null) {
                        setCurrentPage(page, list);
                        if (getCurrentPage() != page) {
                            list.setSelectedValue(getCurrentPage(), true);
                        }
                    }
                    else {
                        list.setSelectedIndex(e.getLastIndex());
                    }
                }
            }
        });
        list.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 10));

        _pageList.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
                for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                    AbstractDialogPage optionsPanel = _pageList.getPage(i);
                    listModel.add(i, optionsPanel);
                }
            }

            public void intervalRemoved(ListDataEvent e) {
                for (int i = e.getIndex1(); i >= e.getIndex0(); i--) {
                    listModel.remove(i);
                }
            }

            public void contentsChanged(ListDataEvent e) {
                if (e.getIndex0() == -1 && e.getIndex1() == -1 && e.getType() == ListDataEvent.CONTENTS_CHANGED) {
                    if (_pageList.getCurrentPage() != null) {
                        int index = _pageList.getPageIndexByFullTitle(_pageList.getCurrentPage().getFullTitle());
                        list.setSelectedIndex(index);
                    }
                }
            }
        });

        JComponent indexPanel = new JPanel(new BorderLayout(4, 4));
        indexPanel.add(new JideScrollPane(list), BorderLayout.CENTER);
        indexPanel.setOpaque(false);
        return indexPanel;
    }

    /**
     * Creates list that is used in LIST_STYLE dialog's index panel. Below is the code we used. If you just want to have
     * a different cell renderer, you can just call {@link #setListCellRenderer(javax.swing.ListCellRenderer)} to set a
     * new one.
     * <pre><code>
     * JList list = new JList(listModel);
     * list.setCellRenderer(createListCellRenderer());
     * return list;
     * </code></pre>
     *
     * @param listModel
     * @return list.
     */
    protected JList createList(DefaultListModel listModel) {
        JList list = new JList(listModel);
        list.setCellRenderer(createListCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return list;
    }

    /**
     * Creates the panel that contains several icons. Each icon represents for a page. This is only used for
     * ICON_STYLE.
     *
     * @return a panel that contains several icons.
     */
    protected JComponent createIconPanel() {
        final ButtonPanel buttonsPanel = createIconButtonPanel();
        buttonsPanel.setGroupGap(0);
        buttonsPanel.setButtonGap(0);

        final ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < _pageList.getPageCount(); i++) {
            final AbstractDialogPage optionsPanel = _pageList.getPage(i);
            final JideButton button = createIconButton(optionsPanel.getTitle(), optionsPanel.getIcon());
            button.setToolTipText(optionsPanel.getDescription());
            button.setEnabled(optionsPanel.isPageEnabled());
            button.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    setCurrentPage(optionsPanel, buttonsPanel);
                    if (getCurrentPage() == optionsPanel) {
                        group.setSelected(button.getModel(), true);
                    }
                }
            });
            optionsPanel.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (AbstractDialogPage.PROPERTY_PAGE_ENABLED.equals(evt.getPropertyName())) {
                        button.setEnabled(Boolean.TRUE.equals(evt.getNewValue()));
                    }
                    else if (AbstractDialogPage.ICON_PROPERTY.equals(evt.getPropertyName())) {
                        button.setIcon((Icon) evt.getNewValue());
                    }
                    else if (AbstractDialogPage.TITLE_PROPERTY.equals(evt.getPropertyName())) {
                        button.setText((String) evt.getNewValue());
                    }
                    else if (AbstractDialogPage.DESCRIPTION_PROPERTY.equals(evt.getPropertyName())) {
                        button.setToolTipText((String) evt.getNewValue());
                    }
                }
            });
            buttonsPanel.addButton(button);
            group.add(button);
            if (_pageList.getPageCount() > 0) {
                if (getInitialPageTitle() != null && getInitialPageTitle().equals(optionsPanel.getFullTitle())) {
                    group.setSelected(button.getModel(), true);
                }
                else if (getInitialPageTitle() == null && i == 0) {
                    group.setSelected(button.getModel(), true);
                }
            }
        }

        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final JScrollPane pane = new JScrollPane(buttonsPanel) {
            @Override
            public Dimension getPreferredSize() {
                if (buttonsPanel.getAlignment() == SwingConstants.TOP || buttonsPanel.getAlignment() == SwingConstants.BOTTOM)
                    return new Dimension(buttonsPanel.getPreferredSize().width + getVerticalScrollBar().getPreferredSize().width, 5);
                else
                    return new Dimension(5, buttonsPanel.getPreferredSize().height + getHorizontalScrollBar().getPreferredSize().height);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };

        if (buttonsPanel.getAlignment() == SwingConstants.TOP || buttonsPanel.getAlignment() == SwingConstants.BOTTOM)
            pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        else
            pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        buttonsPanel.setOpaque(false);

        _pageList.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
                for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                    addPage(i, group, buttonsPanel);
                }
                buttonsPanel.invalidate();
                buttonsPanel.doLayout();
            }

            public void intervalRemoved(ListDataEvent e) {
                for (int i = e.getIndex1(); i >= e.getIndex0(); i--) {
                    AbstractButton button = (AbstractButton) buttonsPanel.getComponent(i);
                    buttonsPanel.remove(button);
                    group.remove(button);
                }
                buttonsPanel.invalidate();
                buttonsPanel.doLayout();
            }

            public void contentsChanged(ListDataEvent e) {
                if (e.getIndex0() == -1 && e.getIndex1() == -1 && e.getType() == ListDataEvent.CONTENTS_CHANGED) {
                    AbstractButton button = (AbstractButton) buttonsPanel.getButtonByName(_pageList.getCurrentPage().getTitle());
                    if (button != null) {
                        group.setSelected(button.getModel(), true);
                    }
                }
            }
        });

        pane.getViewport().setOpaque(false);
        return pane;
    }

    /**
     * Creates the ButtonPanel used by IconPanel. By default, we create it using <code>new
     * ScrollableButtonPanel(SwingConstants.TOP, ButtonPanel.SAME_SIZE)</code>.
     *
     * @return the ButtonPanel.
     */
    protected ButtonPanel createIconButtonPanel() {
        return new ScrollableButtonPanel(SwingConstants.TOP, ButtonPanel.SAME_SIZE);
    }

    private JideButton addPage(int i, final ButtonGroup group, final ButtonPanel buttonsPanel) {
        AbstractDialogPage optionsPanel = _pageList.getPage(i);
        final JideButton button = createIconButton(optionsPanel.getTitle(), optionsPanel.getIcon());
        button.addActionListener(new AbstractAction(optionsPanel.getTitle(), optionsPanel.getIcon()) {
            public void actionPerformed(ActionEvent e) {
                group.setSelected(button.getModel(), true);
                setCurrentPage(_pageList.getPageByFullTitle(e.getActionCommand()), buttonsPanel);
            }
        });
        buttonsPanel.addButton(button, i);
        group.add(button);
        return button;
    }

    /**
     * Creates the button for each icon.
     *
     * @param title
     * @param icon
     * @return the button
     */
    protected JideButton createIconButton(String title, Icon icon) {
        final JideButton button = new JideButton(title, icon);
        button.setName(title);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        return button;
    }

    /**
     * Gets the style of this dialog.
     *
     * @return the style. It can be TAB_STYLE, ICON_STYLE, LIST_STYLE or TREE_STYLE.
     */
    public int getStyle() {
        return _style;
    }

    /**
     * Sets the style of this dialog. This class doesn't support change style on fly. You can only change style before
     * the dialog is set to visible.
     *
     * @param style It must be one of the following: TAB_STYLE, ICON_STYLE, LIST_STYLE or TREE_STYLE.
     */
    public void setStyle(int style) {
        if (style == TAB_STYLE || style == LIST_STYLE || style == ICON_STYLE || style == TREE_STYLE) {
            _style = style;
        }
        else {
            throw new IllegalArgumentException("The value of style must be one of the following - TAB_STYLE, ICON_STYLE, LIST_STYLE or TREE_STYLE");
        }
    }

    /**
     * Gets the index panel.
     *
     * @return the index panel.
     */
    public JComponent getIndexPanel() {
        return _indexPanel;
    }

    /**
     * Gets the pages panel.
     *
     * @return the pages panel.
     */
    public JComponent getPagesPanel() {
        return _pagesPanel;
    }

    /**
     * Gets the cell renderer used by the tree. It's used only when the style is TREE_STYLE.
     *
     * @return the tree cell renderer.
     */
    protected TreeCellRenderer getTreeCellRenderer() {
        return _treeCellRenderer;
    }

    /**
     * Sets the tree cell renderer that will be used by JTree when the style is TREE_STYLE.
     *
     * @param treeCellRenderer
     */
    public void setTreeCellRenderer(TreeCellRenderer treeCellRenderer) {
        _treeCellRenderer = treeCellRenderer;
    }

    /**
     * Gets the cell renderer used by the list. It's used only when the style is LIST_STYLE.
     *
     * @return the list cell renderer.
     */
    protected ListCellRenderer getListCellRenderer() {
        return _listCellRenderer;
    }

    /**
     * Sets the list cell renderer that will be used by JList when the style is LIST_STYLE.
     *
     * @param listCellRenderer
     */
    public void setListCellRenderer(ListCellRenderer listCellRenderer) {
        _listCellRenderer = listCellRenderer;
    }

    /**
     * Creates a list cell renderer used by list in LIST_STYLE dialog's index panel.
     *
     * @return the list cell renderer.
     */
    protected ListCellRenderer createListCellRenderer() {
        if (getListCellRenderer() == null) {
            setListCellRenderer(new DialogPageListCellRenderer());
        }
        return getListCellRenderer();
    }

    /**
     * Creates the tree cell renderer used by tree in TREE_STYLE dialog's index panel.
     *
     * @return the tree cell renderer.
     */
    protected TreeCellRenderer createTreeCellRenderer() {
        if (getTreeCellRenderer() == null) {
            setTreeCellRenderer(new DialogPageTreeCellRenderer());
        }
        return getTreeCellRenderer();
    }

    /**
     * Gets the initial page title. Initial page is the page that will be selected when the dialog is just opened.
     * Please note the title is the full title. In most case it's just the title of the page. Only in TREE_STYLE, it
     * should be a list of titles that concats with '.'.
     *
     * @return the initial page title.
     */
    public String getInitialPageTitle() {
        return _initialPageTitle;
    }

    /**
     * Sets the initial page title. Initial page is the page that will be selected when the dialog.
     *
     * @param initialPageTitle
     */
    public void setInitialPageTitle(String initialPageTitle) {
        _initialPageTitle = initialPageTitle;
    }
}
