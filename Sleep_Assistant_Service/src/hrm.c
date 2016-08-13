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
	appdata_s* ad = (appdata_s *) user_data;
	char buf[PATH_MAX];
	sprintf(buf, "%0.1f", sensor_data->values[0]);
	dlog_print(DLOG_INFO, "%s", buf);
	send_data(buf);
	//elm_object_text_set(ad->peekLabel, buf2);
}

void
start_heartrate_sensor(appdata_s *ad)
{

	if (sensor_get_default_sensor(SENSOR_HRM, &(ad->sensor)) == SENSOR_ERROR_NONE)
	{
		if (sensor_create_listener(ad->sensor, &(ad->listener)) == SENSOR_ERROR_NONE
				&& sensor_listener_set_event_cb(ad->listener, 3000, _HRM_value, ad) == SENSOR_ERROR_NONE
				&& sensor_listener_set_option(ad->listener, SENSOR_OPTION_ALWAYS_ON) == SENSOR_ERROR_NONE)
		{
			if (sensor_listener_start(ad->listener) == SENSOR_ERROR_NONE)
			{
				//LOGI("Sensor listener started.");
			}
		}
	}
	/*
	sensor_error_e err = SENSOR_ERROR_NONE;
	sensor_get_default_sensor(SENSOR_HRM, &HRM_info.sensor);
	err = sensor_create_listener(HRM_info.sensor, &HRM_info.sensor_listener);
	sensor_listener_set_option(HRM_info.sensor_listener,SENSOR_OPTION_ALWAYS_ON);
	sensor_listener_set_event_cb(HRM_info.sensor_listener, 5000, _HRM_value, (void *)0);
	//sensor_listener_set_event_cb()
	sensor_listener_start(HRM_info.sensor_listener);
	*/
}


void
stop_heartrate_sensor(appdata_s *ad)
{
	sensor_listener_stop(ad->listener);
}
