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

package com.manydesigns.elements.fields;

import com.manydesigns.elements.FormElement;
import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.elements.xml.XhtmlBuffer;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public interface Field extends FormElement {
    public static final String copyright =
            "Copyright (c) 2005-2013, ManyDesigns srl";

    PropertyAccessor getPropertyAccessor();

    void valueToXhtml(XhtmlBuffer xb);
    void labelToXhtml(XhtmlBuffer xb);
    void helpToXhtml(XhtmlBuffer xb);
    void errorsToXhtml(XhtmlBuffer xb);

    public String  getId();
    void setId(String id);

    String getInputName();
    void setInputName(String inputName);

    boolean isRequired();
    void setRequired(boolean required);

    String getHref();
    void setHref(String href);

    String getTitle();
    void setTitle(String alt);

    String getStringValue();

    boolean isEnabled();
    void setEnabled(boolean enabled);

    boolean isInsertable();
    void setInsertable(boolean insertable);

    boolean isUpdatable();
    void setUpdatable(boolean updatable);

    Object getValue();

    String getDisplayValue();
}
