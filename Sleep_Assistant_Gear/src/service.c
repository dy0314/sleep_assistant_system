/*
 * service.c
 *
 *  Created on: Aug 13, 2016
 *      Author: KimNam
 */

#include "main.h"

void start_service()
{
    app_control_h app_control;
	if (app_control_create(&app_control)== APP_CONTROL_ERROR_NONE)
	{
		if ((app_control_set_app_id(app_control, "org.tizen.sleep_assistant_service") == APP_CONTROL_ERROR_NONE)
			&& (app_control_add_extra_data(app_control, "service_action", "start") == APP_CONTROL_ERROR_NONE)
			&& (app_control_send_launch_request(app_control, NULL, NULL) == APP_CONTROL_ERROR_NONE))
		{
			//LOGI("App launch request sent!");
		}
		else
		{
			//LOGE("App launch request sending failed!");
		}
		if (app_control_destroy(app_control) == APP_CONTROL_ERROR_NONE)
		{
		//	LOGI("App control destroyed.");
		}
		//ui_app_exit();
	}
	else
	{
		//LOGE("App control creation failed!");
	}
}

void stop_service()
{
    app_control_h app_control;
	if (app_control_create(&app_control)== APP_CONTROL_ERROR_NONE)
	{
		if ((app_control_set_app_id(app_control, "org.tizen.sleep_assistant_service") == APP_CONTROL_ERROR_NONE)
			&& (app_control_add_extra_data(app_control, "service_action", "stop") == APP_CONTROL_ERROR_NONE)
			&& (app_control_send_launch_request(app_control, NULL, NULL) == APP_CONTROL_ERROR_NONE))
		{
			//LOGI("App launch request sent!");
		}
		else
		{
			//LOGE("App launch request sending failed!");
		}
		if (app_control_destroy(app_control) == APP_CONTROL_ERROR_NONE)
		{
		//	LOGI("App control destroyed.");
		}
		//ui_app_exit();
	}
	else
	{
		//LOGE("App control creation failed!");
	}
}

void exit_service()
{
    app_control_h app_control;
	if (app_control_create(&app_control)== APP_CONTROL_ERROR_NONE)
	{
		if ((app_control_set_app_id(app_control, "org.tizen.sleep_assistant_service") == APP_CONTROL_ERROR_NONE)
			&& (app_control_add_extra_data(app_control, "service_action", "exit") == APP_CONTROL_ERROR_NONE)
			&& (app_control_send_launch_request(app_control, NULL, NULL) == APP_CONTROL_ERROR_NONE))
		{
			//LOGI("App launch request sent!");
		}
		else
		{
			//LOGE("App launch request sending failed!");
		}
		if (app_control_destroy(app_control) == APP_CONTROL_ERROR_NONE)
		{
		//	LOGI("App control destroyed.");
		}
		//ui_app_exit();
	}
	else
	{
		//LOGE("App control creation failed!");
	}
}
