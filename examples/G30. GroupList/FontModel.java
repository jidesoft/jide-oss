/*
 * FontModel.java
 *
 * Created on Oct 16, 2007, 11:59:02 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jidesoft.list.AbstractGroupListModel;
import com.jidesoft.list.AbstractGroupableListModel;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class FontModel extends AbstractGroupableListModel {

    private static final String[] FONT_NAMES =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    private static String[] GROUP_NAMES = {"Recently Used Fonts", "All Fonts"};

    private List<Font> _fonts;
    private int _limit;
    private List<Font> _recentlyUsed;

    public FontModel() {
        this(8);
    }

    @SuppressWarnings("unchecked")
    public FontModel(int limit) {
        _limit = limit;
        _recentlyUsed = new LinkedList();
        _fonts = new ArrayList();
        for(String fontName: FONT_NAMES) {
            _fonts.add(Font.decode(fontName));
        }
    }

    public Object getGroupAt(int index) {
        if (index < _recentlyUsed.size()) {
            return GROUP_NAMES[0];
        }
        else {
            return GROUP_NAMES[1];
        }
    }

    public int getSize() {
        return _recentlyUsed.size() + _fonts.size();
    }

    public Object getElementAt(int index) {
        int usedSize = _recentlyUsed.size();
        if (index < usedSize) {
            return _recentlyUsed.get(index);
        }
        return _fonts.get(index - usedSize);
    }

    public List<Font> getRecentlyUsedFont() {
        return Collections.unmodifiableList(_recentlyUsed);
    }

    public void putFont(Font font) {
        if (_recentlyUsed.contains(font)) {
            _recentlyUsed.remove(font);
        }
        _recentlyUsed.add(0, font);
        if (_recentlyUsed.size() > _limit) {
            _recentlyUsed.remove(_recentlyUsed.size() - 1);
        }
        super.fireGroupChanged(this);
    }

    public Object[] getGroups() {
        return GROUP_NAMES;
    }

    public void remove(int index) {
        int usedSize = _recentlyUsed.size();
        if (index < usedSize) {
            _recentlyUsed.remove(index);
        }
        else {
            _fonts.remove(index - usedSize);
        }
        fireGroupChanged(this);
    }

    public void shuffle() {
        Collections.shuffle(_recentlyUsed);
        Collections.shuffle(_fonts);
        fireGroupChanged(this);
    }

}

class FontModel2 extends AbstractGroupListModel {

    private static final Font[] FONTS =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

    private static String[] GROUP_NAMES = {"Recently Used Fonts", "All Fonts"};

    private List<Font> _fonts;
    private int _limit;
    private List<Font> _recentlyUsed;

    public FontModel2() {
        this(8);
    }

    @SuppressWarnings("unchecked")
    public FontModel2(int limit) {
        _limit = limit;
        _fonts = new LinkedList(Arrays.asList(FONTS));
        _recentlyUsed = new LinkedList();
    }

    public int getSize() {
        return _fonts.size();
    }

    public Object getElementAt(int index) {
        int start = 0;
        if(index < start) {
            throw new IllegalArgumentException();
        }
        if(index == start) {
            return GROUP_NAMES[0];
        }
        start++;
        if(index < start + _recentlyUsed.size()) {
            return _recentlyUsed.get(index - start);
        }
        start += _recentlyUsed.size();
        if(index == start) {
            return GROUP_NAMES[1];
        }
        start++;
        if(index < start + _fonts.size()) {
            return _fonts.get(index - start);
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    public void putFont(Font font) {
        int index = _recentlyUsed.indexOf(font);
        if(index != -1) {
            _recentlyUsed.remove(index);
        }
        _recentlyUsed.add(0, font);
        if (_recentlyUsed.size() > _limit) {
            _recentlyUsed.remove(_recentlyUsed.size() - 1);
            super.fireContentsChanged(this, 1, _recentlyUsed.size());
        }
        else {
            if(index != -1) {
                super.fireContentsChanged(this, 1, 1 + index);
            }
            else {
                super.fireIntervalAdded(this, 1, 1);
            }
        }
    }

    public void remove(int index) {
        int start = 0;
        if(index < start) {
            throw new IllegalArgumentException();
        }
        if(index == start) {
            throw new IllegalArgumentException();
        }
        start++;
        if(index < start + _recentlyUsed.size()) {
            _recentlyUsed.remove(index - start);
        }
        start += _recentlyUsed.size();
        if(index == start) {
            throw new IllegalArgumentException();
        }
        start++;
        if(index < start + _fonts.size()) {
            _fonts.remove(index - start);
        }
        throw new IllegalArgumentException();
    }

    public boolean isGroupRow(int index) {
        return index == 0 || index == _recentlyUsed.size() + 1;
    }

    @Override
    public int getGroupRowIndex(int index) {
        return index < _recentlyUsed.size() + 1 ? 0 : _recentlyUsed.size() + 1;
    }

    @Override
    public int getNextGroupRowIndex(int index) {
        return index < _recentlyUsed.size() + 1 ? _recentlyUsed.size() + 1 : _fonts.size();
    }

    public void shuffle() {
        Collections.shuffle(_recentlyUsed);
        Collections.shuffle(_fonts);
        fireContentsChanged(this, 1, getSize());
    }

}
