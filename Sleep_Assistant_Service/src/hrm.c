/*
 * hrm.c
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#include "main.h"

double dabs(double x) {
	if(x < 0)
		return -x;
	return x;
}

void _HRM_value(sensor_h sensor, sensor_event_s *sensor_data, void
		*user_data)
{
	char buf[PATH_MAX];
	appdata_s *ad = (appdata_s *) user_data;
	//count++;
	ad->real_count++;

	if(sensor_data->values[0] > 10) {
		//real_count++;
		//value += sensor_data->values[0];
		//sprintf(buf, "%0.1f %0.1f %0.1f %0.1f", sensor_data->values[0], x, y, z);
		ad->count++;
		ad->heart_rate += sensor_data->values[0];
	}
	if(ad->real_count == 10) {
		if(ad->count < 7) {
			sprintf(buf, "-1");
			send_data(buf);
		} else {
			sprintf(buf, "%0.1f", ((ad->heart_rate) / (ad->count)));
			send_data(buf);
		}
		ad->count = 0;
		ad->real_count = 0;
		ad->heart_rate = 0;
	}

	/*
	sprintf(buf, "%f %f %f\n%f %f %f\n", ad->savex, ad->savey, ad->savez, ad->x, ad->y, ad->z);
	send_data(buf);
	*/
	/*
	if(diff > 0.8) {
		sprintf(buf, "move");
		send_data(buf);
	}
	else
	{
		sprintf(buf, "not move");
		send_data(buf);
	}
	*/

	/*
	if(count == 5) {
		sprintf(buf, "%0.1f", (value / real_count));
		send_data(buf);
		real_count = 0;
		count = 0;
		value = 0;
	}
	*/
	//elm_object_text_set(ad->peekLabel, buf2);
}

void
start_heartrate_sensor(appdata_s *ad)
{
	//start_acceleration_sensor(ad);
	if (sensor_get_default_sensor(SENSOR_HRM, &(ad->sensor)) == SENSOR_ERROR_NONE)
	{
		if (sensor_create_listener(ad->sensor, &(ad->listener)) == SENSOR_ERROR_NONE
				&& sensor_listener_set_event_cb(ad->listener, 1000, _HRM_value, ad) == SENSOR_ERROR_NONE
				&& sensor_listener_set_option(ad->listener, SENSOR_OPTION_ALWAYS_ON) == SENSOR_ERROR_NONE)
		{
			//sensor_listener_set_interval(ad->listener, 3000);
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
	//stop_acceleration_sensor(ad);
	sensor_listener_stop(ad->listener);
}
