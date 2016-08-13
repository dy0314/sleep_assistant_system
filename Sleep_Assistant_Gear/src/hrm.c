/*
 * hrm.c
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#include "main.h"

void _HRM_value(sensor_h sensor, sensor_event_s *sensor_data, void
		*user_data)
{
	char buf[PATH_MAX];
	char buf2[PATH_MAX];
	char buf3[PATH_MAX];
	char buf4[PATH_MAX];
	appdata_s *ad = (appdata_s*)user_data;
	sprintf(buf, "<align=center> <font_size=20> Heart Rate : %0.1f </font> </align>", sensor_data->values[0]);
	ad->currentHRM = sensor_data->values[0];
	elm_object_text_set(ad->HRMLabel, buf);

	if(ad->currentHRM >= 10 && ad->currentHRM < 60)
		ad->HRM50++;
	else if(ad->currentHRM >= 75)
		ad->HRM75++;
	ad->check++;
	sprintf(buf4, "%f", ad->currentHRM);
	app_control_h app_control;
	if (app_control_create(&app_control)== APP_CONTROL_ERROR_NONE)
	{
		if ((app_control_set_app_id(app_control, "org.tizen.sleep_assistant_service") == APP_CONTROL_ERROR_NONE)
				&& (app_control_add_extra_data(app_control, "service_action", buf4) == APP_CONTROL_ERROR_NONE)
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
			//LOGI("App control destroyed.");
		}
	}
	//sprintf(buf2, "<align=center> <font_size=20> Under 60 count : %d </font> </align>", ad->HRM50);
	sprintf(buf2, "<align=center> <font_size=20> Checker : %d </font> </align>", ad->check);
	//sprintf(buf2, "<align=center> <font_size=20> Peek-to-Peek : %0.1f </font> </align>", sensor_data->values[2]);
	elm_object_text_set(ad->check50Label, buf2);

	sprintf(buf3, "<align=center> <font_size=20> Over 75 count : %d </font> </align>", ad->HRM75);
	elm_object_text_set(ad->check75Label, buf3);

	//elm_object_text_set(ad->peekLabel, buf2);
}

void
start_heartrate_sensor(appdata_s *ad)
{
	sensor_error_e err = SENSOR_ERROR_NONE;
	sensor_get_default_sensor(SENSOR_HRM, &HRM_info.sensor);
	err = sensor_create_listener(HRM_info.sensor, &HRM_info.sensor_listener);
	sensor_listener_set_option(HRM_info.sensor_listener,SENSOR_OPTION_ALWAYS_ON);
	sensor_listener_set_event_cb(HRM_info.sensor_listener, 5000, _HRM_value, ad);

	//sensor_listener_set_event_cb()
	sensor_listener_start(HRM_info.sensor_listener);
}

void
stop_heartrate_sensor(appdata_s *ad)
{
  sensor_listener_stop(HRM_info.sensor_listener);
}
