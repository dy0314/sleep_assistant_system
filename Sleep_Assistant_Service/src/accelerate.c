/*
 * accelerate.c
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#include "main.h"

void _accerleration_value(sensor_h sensor, sensor_event_s *sensor_data, void
		*user_data)
{

	if( sensor_data->value_count < 3 )
		return;
	char buf[PATH_MAX];
	appdata_s *ad = (appdata_s *) user_data;
	/*
	if(ad->chk == 0) {
		ad->savex = sensor_data->values[0];
		ad->savey = sensor_data->values[1];
		ad->savez = sensor_data->values[2];
		ad->chk = 1;
	} else {
		ad->x = sensor_data->values[0];
		ad->y = sensor_data->values[1];
		ad->z = sensor_data->values[2];
	}
	 */
	//sprintf(buf, "X : %0.1f / Y : %0.1f / Z : %0.1f ", sensor_data->values[0], sensor_data->values[1], sensor_data->values[2]);

	//send_data(buf);
}

void
start_acceleration_sensor(appdata_s *ad)
{
	if (sensor_get_default_sensor(SENSOR_LINEAR_ACCELERATION, &(ad->sensor)) == SENSOR_ERROR_NONE)
		{
			if (sensor_create_listener(ad->sensor, &(ad->listener)) == SENSOR_ERROR_NONE
					&& sensor_listener_set_event_cb(ad->listener, 3000, _accerleration_value, ad) == SENSOR_ERROR_NONE
					&& sensor_listener_set_option(ad->listener, SENSOR_OPTION_ALWAYS_ON) == SENSOR_ERROR_NONE)
			{
				if (sensor_listener_start(ad->listener) == SENSOR_ERROR_NONE)
				{
					//LOGI("Sensor listener started.");
				}
			}
		}
}

void
stop_acceleration_sensor(appdata_s *ad)
{
	sensor_listener_stop(ad->listener);
}

