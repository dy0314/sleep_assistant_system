/*
 * main.c
 *
 *  Created on: Aug 8, 2016
 *      Author: KimNam
 */

#include "main.h"

void update_ui(char *data)
{
	dlog_print(DLOG_INFO, TAG, "Updating UI with data %s", data);
	//_popup_toast_cb(object->naviframe, data);
}

bool service_app_create(void *data)
{
    // Todo: add your code here.
	appdata_s* ad = (appdata_s *)data;;
	dlog_print(DLOG_INFO, TAG, "Shit 3");
	initialize_sap();
	stop_heartrate_sensor(ad);
	//service_app_exit();
    return true;
}

void service_app_terminate(void *data)
{
    // Todo: add your code here.
	appdata_s *ad = (appdata_s *)data;
	stop_heartrate_sensor(ad);
    return;
}

void service_app_control(app_control_h app_control, void *data)
{
	// Todo: add your code here.

	char *caller_id = NULL, *action_value = NULL;
	appdata_s *ad = (appdata_s *) data;
	if ((app_control_get_caller(app_control, &caller_id) == APP_CONTROL_ERROR_NONE)
			&& (app_control_get_extra_data(app_control, "service_action", &action_value) == APP_CONTROL_ERROR_NONE))
	{
		if((caller_id != NULL) && (action_value != NULL)
				&& (!strncmp(caller_id, GEAR_APP_ID, STRNCMP_LIMIT))
				&& (!strncmp(action_value, "exit", STRNCMP_LIMIT)))
		{
			free(caller_id);
			free(action_value);
			service_app_exit();
			return;
		}
		else if ((caller_id != NULL) && (action_value != NULL)
				&& (!strncmp(caller_id, GEAR_APP_ID, STRNCMP_LIMIT))
				&& (!strncmp(action_value,"start", STRNCMP_LIMIT)))
		{
			start_heartrate_sensor(ad);
		}
		else if ((caller_id != NULL) && (action_value != NULL)
				&& (!strncmp(caller_id, GEAR_APP_ID, STRNCMP_LIMIT))
				&& (!strncmp(action_value,"stop", STRNCMP_LIMIT)))
		{
			stop_heartrate_sensor(ad);
		}
	}
	return;
}

static void
service_app_lang_changed(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_LANGUAGE_CHANGED*/
	return;
}

static void
service_app_region_changed(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_REGION_FORMAT_CHANGED*/
}

static void
service_app_low_battery(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_LOW_BATTERY*/
}

static void
service_app_low_memory(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_LOW_MEMORY*/
}

int main(int argc, char* argv[])
{
    char ad[50] = {0,};
	service_app_lifecycle_callback_s event_callback;
	app_event_handler_h handlers[5] = {NULL, };

	event_callback.create = service_app_create;
	event_callback.terminate = service_app_terminate;
	event_callback.app_control = service_app_control;

	service_app_add_event_handler(&handlers[APP_EVENT_LOW_BATTERY], APP_EVENT_LOW_BATTERY, service_app_low_battery, &ad);
	service_app_add_event_handler(&handlers[APP_EVENT_LOW_MEMORY], APP_EVENT_LOW_MEMORY, service_app_low_memory, &ad);
	service_app_add_event_handler(&handlers[APP_EVENT_LANGUAGE_CHANGED], APP_EVENT_LANGUAGE_CHANGED, service_app_lang_changed, &ad);
	service_app_add_event_handler(&handlers[APP_EVENT_REGION_FORMAT_CHANGED], APP_EVENT_REGION_FORMAT_CHANGED, service_app_region_changed, &ad);

	return service_app_main(argc, argv, &event_callback, ad);
}
