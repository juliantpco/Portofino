/*
 * Copyright (C) 2005-2013 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * There are special exceptions to the terms and conditions of the GPL
 * as it is applied to this software. View the full text of the
 * exception in file OPEN-SOURCE-LICENSE.txt in the directory of this
 * software distribution.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307  USA
 *
 */

package com.manydesigns.portofino.security;

/**
 * Enumerates the possible <i>access levels</i> to a page. In order of priority:
 * <ul>
 *     <li><strong><code>NONE</code></strong> - neither grants, nor forbids access.</li>
 *     <li><strong><code>VIEW</code></strong> - grants access to the page in read-only mode.</li>
 *     <li><strong><code>EDIT</code></strong> - grants access to the page in edit mode: some operations are permitted
 *     (depending on the type of page), but at least modifying the Groovy source code of the page is forbidden.</li>
 *     <li><strong><code>DEVELOP</code></strong> - grants every permissions except those reserved for the administrator
 *     (superuser), including that of editing the Groovy source code of the page.</li>
 *     <li><strong><code>DENY</code></strong> - denies access to the page.</li>
 * </ul>
 *
 * The level with the greatest priority wins over the others.
 *
 * @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
 * @author Angelo Lupo          - angelo.lupo@manydesigns.com
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Alessio Stalla       - alessio.stalla@manydesigns.com
 */
public enum AccessLevel {

    NONE(0), VIEW(1), EDIT(2), DEVELOP(3), DENY(Integer.MAX_VALUE);

    private AccessLevel(int level) {
        this.level = level;
    }

    public boolean isGreaterThanOrEqual(AccessLevel accessLevel) {
        return level >= accessLevel.level;
    }

    private final int level;

    public static final String copyright=
            "Copyright (c) 2005-2013, ManyDesigns srl";
}
