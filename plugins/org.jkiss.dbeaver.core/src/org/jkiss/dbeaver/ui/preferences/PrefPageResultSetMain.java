/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2016 Serge Rieder (serge@jkiss.org)
 * Copyright (C) 2011-2012 Eugene Fradkin (eugene.fradkin@gmail.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (version 2)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jkiss.dbeaver.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.jkiss.dbeaver.DBeaverPreferences;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.core.CoreMessages;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPPreferenceStore;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.utils.PrefUtils;

/**
 * PrefPageResultSetMain
 */
public class PrefPageResultSetMain extends TargetPrefPage
{
    public static final String PAGE_ID = "org.jkiss.dbeaver.preferences.main.resultset"; //$NON-NLS-1$

    private Button autoFetchNextSegmentCheck;
    private Spinner resultSetSize;
    private Button resultSetUseSQLCheck;
    private Button serverSideOrderingCheck;
    private Button useFetchSize;
    private Button readQueryMetadata;
    private Button readQueryReferences;
    private Spinner queryCancelTimeout;

    private Button keepStatementOpenCheck;
    private Button rollbackOnErrorCheck;
    private Button alwaysUseAllColumns;


    public PrefPageResultSetMain()
    {
        super();
    }

    @Override
    protected boolean hasDataSourceSpecificOptions(DBPDataSourceContainer dataSourceDescriptor)
    {
        DBPPreferenceStore store = dataSourceDescriptor.getPreferenceStore();
        return
            store.contains(DBeaverPreferences.RESULT_SET_AUTO_FETCH_NEXT_SEGMENT) ||
            store.contains(DBeaverPreferences.RESULT_SET_MAX_ROWS) ||
            store.contains(ModelPreferences.RESULT_SET_MAX_ROWS_USE_SQL) ||
            store.contains(ModelPreferences.RESULT_SET_USE_FETCH_SIZE) ||
            store.contains(DBeaverPreferences.RESULT_SET_READ_METADATA) ||
            store.contains(DBeaverPreferences.RESULT_SET_CANCEL_TIMEOUT) ||
            store.contains(ModelPreferences.QUERY_ROLLBACK_ON_ERROR) ||
            store.contains(DBeaverPreferences.RS_EDIT_USE_ALL_COLUMNS) ||
            store.contains(DBeaverPreferences.KEEP_STATEMENT_OPEN) ||
            store.contains(DBeaverPreferences.RESULT_SET_ORDER_SERVER_SIDE)
            ;
    }

    @Override
    protected boolean supportsDataSourceSpecificOptions()
    {
        return true;
    }

    @Override
    protected Control createPreferenceContent(Composite parent)
    {
        Composite composite = UIUtils.createPlaceholder(parent, 1, 5);

        {
            Group queriesGroup = UIUtils.createControlGroup(composite, CoreMessages.pref_page_database_general_group_queries, 2, SWT.NONE, 0);
            queriesGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

            resultSetSize = UIUtils.createLabelSpinner(queriesGroup, CoreMessages.pref_page_database_general_label_result_set_max_size, "", 0, 0, 1024 * 1024);
            autoFetchNextSegmentCheck = UIUtils.createCheckbox(queriesGroup, CoreMessages.pref_page_database_resultsets_label_auto_fetch_segment, null, true, 2);
            resultSetUseSQLCheck = UIUtils.createCheckbox(queriesGroup, CoreMessages.pref_page_database_resultsets_label_use_sql, null, false, 2);
            serverSideOrderingCheck = UIUtils.createCheckbox(queriesGroup, CoreMessages.pref_page_database_resultsets_label_server_side_order, null, false, 2);
            useFetchSize = UIUtils.createCheckbox(queriesGroup, CoreMessages.pref_page_database_resultsets_label_fetch_size, null, false, 2);
            readQueryMetadata = UIUtils.createCheckbox(queriesGroup, CoreMessages.pref_page_database_resultsets_label_read_metadata,
                "Disables metadata read. Executes query faster but disables results edit and foreign key navigation", false, 2);
            readQueryReferences = UIUtils.createCheckbox(queriesGroup, CoreMessages.pref_page_database_resultsets_label_read_references,
                "Disables references (foreign keys) information reading.", false, 2);
            queryCancelTimeout = UIUtils.createLabelSpinner(queriesGroup, CoreMessages.pref_page_database_general_label_result_set_cancel_timeout, CoreMessages.pref_page_database_general_label_result_set_cancel_timeout_tip, 0, 0, Integer.MAX_VALUE);
            queryCancelTimeout.setEnabled(false);

            readQueryMetadata.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    updateOptionsEnablement();
                }
            });
        }

        // Transactions settings
        {
            Group txnGroup = new Group(composite, SWT.NONE);
            txnGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

            txnGroup.setText(CoreMessages.pref_page_sql_editor_group_misc);
            txnGroup.setLayout(new GridLayout(1, false));

            keepStatementOpenCheck = UIUtils.createCheckbox(txnGroup, CoreMessages.pref_page_database_general_checkbox_keep_cursor, false);
            rollbackOnErrorCheck = UIUtils.createCheckbox(txnGroup, CoreMessages.pref_page_database_general_checkbox_rollback_on_error, false);
            alwaysUseAllColumns = UIUtils.createCheckbox(txnGroup, CoreMessages.pref_page_content_editor_checkbox_keys_always_use_all_columns, false);
        }

        return composite;
    }

    private void updateOptionsEnablement() {
        readQueryReferences.setEnabled(readQueryMetadata.getSelection());
    }

    @Override
    protected void loadPreferences(DBPPreferenceStore store)
    {
        try {
            autoFetchNextSegmentCheck.setSelection(store.getBoolean(DBeaverPreferences.RESULT_SET_AUTO_FETCH_NEXT_SEGMENT));
            resultSetSize.setSelection(store.getInt(DBeaverPreferences.RESULT_SET_MAX_ROWS));
            resultSetUseSQLCheck.setSelection(store.getBoolean(ModelPreferences.RESULT_SET_MAX_ROWS_USE_SQL));
            serverSideOrderingCheck.setSelection(store.getBoolean(DBeaverPreferences.RESULT_SET_ORDER_SERVER_SIDE));
            useFetchSize.setSelection(store.getBoolean(ModelPreferences.RESULT_SET_USE_FETCH_SIZE));
            readQueryMetadata.setSelection(store.getBoolean(DBeaverPreferences.RESULT_SET_READ_METADATA));
            readQueryReferences.setSelection(store.getBoolean(DBeaverPreferences.RESULT_SET_READ_REFERENCES));
            queryCancelTimeout.setSelection(store.getInt(DBeaverPreferences.RESULT_SET_CANCEL_TIMEOUT));

            keepStatementOpenCheck.setSelection(store.getBoolean(DBeaverPreferences.KEEP_STATEMENT_OPEN));
            rollbackOnErrorCheck.setSelection(store.getBoolean(ModelPreferences.QUERY_ROLLBACK_ON_ERROR));
            alwaysUseAllColumns.setSelection(store.getBoolean(DBeaverPreferences.RS_EDIT_USE_ALL_COLUMNS));

            updateOptionsEnablement();
        } catch (Exception e) {
            log.warn(e);
        }
    }

    @Override
    protected void savePreferences(DBPPreferenceStore store)
    {
        try {
            store.setValue(DBeaverPreferences.RESULT_SET_AUTO_FETCH_NEXT_SEGMENT, autoFetchNextSegmentCheck.getSelection());
            store.setValue(DBeaverPreferences.RESULT_SET_MAX_ROWS, resultSetSize.getSelection());
            store.setValue(ModelPreferences.RESULT_SET_MAX_ROWS_USE_SQL, resultSetUseSQLCheck.getSelection());
            store.setValue(DBeaverPreferences.RESULT_SET_ORDER_SERVER_SIDE, serverSideOrderingCheck.getSelection());
            store.setValue(DBeaverPreferences.RESULT_SET_READ_METADATA, readQueryMetadata.getSelection());
            store.setValue(DBeaverPreferences.RESULT_SET_READ_REFERENCES, readQueryReferences.getSelection());
            store.setValue(ModelPreferences.RESULT_SET_USE_FETCH_SIZE, useFetchSize.getSelection());
            store.setValue(DBeaverPreferences.RESULT_SET_CANCEL_TIMEOUT, queryCancelTimeout.getSelection());

            store.setValue(DBeaverPreferences.KEEP_STATEMENT_OPEN, keepStatementOpenCheck.getSelection());
            store.setValue(ModelPreferences.QUERY_ROLLBACK_ON_ERROR, rollbackOnErrorCheck.getSelection());
            store.setValue(DBeaverPreferences.RS_EDIT_USE_ALL_COLUMNS, alwaysUseAllColumns.getSelection());
        } catch (Exception e) {
            log.warn(e);
        }
        PrefUtils.savePreferenceStore(store);
    }

    @Override
    protected void clearPreferences(DBPPreferenceStore store)
    {
        store.setToDefault(DBeaverPreferences.RESULT_SET_AUTO_FETCH_NEXT_SEGMENT);
        store.setToDefault(DBeaverPreferences.RESULT_SET_MAX_ROWS);
        store.setToDefault(ModelPreferences.RESULT_SET_MAX_ROWS_USE_SQL);
        store.setToDefault(DBeaverPreferences.RESULT_SET_ORDER_SERVER_SIDE);
        store.setToDefault(DBeaverPreferences.RESULT_SET_READ_METADATA);
        store.setToDefault(DBeaverPreferences.RESULT_SET_READ_REFERENCES);
        store.setToDefault(ModelPreferences.RESULT_SET_USE_FETCH_SIZE);
        store.setToDefault(DBeaverPreferences.RESULT_SET_CANCEL_TIMEOUT);

        store.setToDefault(DBeaverPreferences.KEEP_STATEMENT_OPEN);
        store.setToDefault(ModelPreferences.QUERY_ROLLBACK_ON_ERROR);
        store.setToDefault(DBeaverPreferences.RS_EDIT_USE_ALL_COLUMNS);

        updateOptionsEnablement();
    }

    @Override
    protected String getPropertyPageID()
    {
        return PAGE_ID;
    }

}