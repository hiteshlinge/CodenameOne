/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *  
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Codename One through http://www.codenameone.com/ if you 
 * need additional information or have any questions.
 */
package com.codename1.share;

import com.codename1.components.MultiButton;
import com.codename1.contacts.ContactsManager;
import com.codename1.contacts.ContactsModel;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.List;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.GenericListCellRenderer;
import com.codename1.ui.list.ListCellRenderer;
import java.io.IOException;
import java.util.Hashtable;

/**
 * SMS Sharing service
 * @author Chen
 */
public class SMSShare extends ShareService {

    /**
     * Default Constructor
     */
    public SMSShare() {
        super("SMS", null);
    }
    
    /**
     * @inheritDoc
     */
    public void share(final String toShare) {
        final Form currentForm = Display.getInstance().getCurrent();
        final Form f = new Form("Contacts");
        f.setScrollable(false);        
        f.setLayout(new BorderLayout());
        f.addComponent(BorderLayout.CENTER, new Label("Please wait..."));
        f.show();
        new Thread(new Runnable() {

            public void run() {
                String[] ids = ContactsManager.getAllContactsWithNumbers();
                ContactsModel model = new ContactsModel(ids);
                final List contacts = new List(model);
                contacts.setRenderer(createListRenderer());
                Display.getInstance().callSerially(new Runnable() {

                    public void run() {

                        contacts.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent evt) {
                                final ShareForm [] f = new ShareForm[1];
                                final Hashtable contact = (Hashtable) contacts.getSelectedItem();
                                
                                f[0] = new ShareForm("Send SMS", (String)contact.get("phone"), toShare,
                                        new ActionListener() {

                                            public void actionPerformed(ActionEvent evt) {
                                                try {
                                                    Display.getInstance().sendSMS(f[0].getTo(), f[0].getMessage());
                                                } catch (IOException ex) {
                                                    ex.printStackTrace();
                                                    System.out.println("failed to send sms to " + (String)contact.get("phone"));
                                                }
                                                currentForm.show();
                                            }
                                        });
                                f[0].show();
                                
                            }
                        });
                        f.addComponent(BorderLayout.CENTER, contacts);
                        f.revalidate();
                    }
                });
            }
        }).start();
    }
    
    private MultiButton createRendererMultiButton() {
        MultiButton b = new MultiButton();
        b.setIconName("icon");
        b.setNameLine1("displayName");
        b.setNameLine2("phone");
        b.setUIID("Label");
        return b;
    }
    
    private ListCellRenderer createListRenderer() {
        MultiButton sel = createRendererMultiButton();
        MultiButton unsel = createRendererMultiButton();
        return new GenericListCellRenderer(sel, unsel);
    }
    
}