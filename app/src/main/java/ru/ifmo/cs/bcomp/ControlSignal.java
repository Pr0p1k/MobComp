/*
 * $Id$
 */

package ru.ifmo.cs.bcomp;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */
public enum ControlSignal {
	/**
	 * Управляющий сигнал 0: Halt
	 */
	HALT,
	/**
	 * Управляющий сигнал 1: РД -> Правый вход АЛУ
	 */
	DATA_TO_ALU,
	/**
	 * Управляющий сигнал 2: РК -> Правый вход АЛУ
	 */
	INSTR_TO_ALU,
	/**
	 * Управляющий сигнал 3: СК -> Правый вход АЛУ
	 */
	IP_TO_ALU,
	/**
	 * Управляющий сигнал 4: А -> Левый вход АЛУ
	 */
	ACCUM_TO_ALU,
	/**
	 * Управляющий сигнал 5: РС -> Левый вход АЛУ
	 */
	STATE_TO_ALU,
	/**
	 * Управляющий сигнал 6: КлР -> Левый вход АЛУ
	 */
	KEY_TO_ALU,
	/**
	 * Управляющий сигнал 7: Левый вход: инверсия
	 */
	INVERT_LEFT,
	/**
	 * Управляющий сигнал 8: Правый вход: инверсия
	 */
	INVERT_RIGHT,
	/**
	 * Управляющий сигнал 9: АЛУ: + или &
	 */
	ALU_AND,
	/**
	 * Управляющий сигнал 10: АЛУ: +1
	 */
	ALU_PLUS_1,
	/**
	 * Управляющий сигнал 11: Сдвиг вправо
	 */
	SHIFT_RIGHT,
	/**
	 * Управляющий сигнал 12: Сдвиг влево
	 */
	SHIFT_LEFT,
	/**
	 * Управляющий сигнал 13: БР(16) -> С
	 */
	BUF_TO_STATE_C,
	/**
	 * Управляющий сигнал 14: БР(15) -> N
	 */
	BUF_TO_STATE_N,
	/**
	 * Управляющий сигнал 15: БР == 0 -> Z
	 */
	BUF_TO_STATE_Z,
	/**
	 * Управляющий сигнал 16: 0 -> С
	 */
	CLEAR_STATE_C,
	/**
	 * Управляющий сигнал 17: 1 -> С
	 */
	SET_STATE_C,
	/**
	 * Управляющий сигнал 18: БР -> РА
	 */
	BUF_TO_ADDR,
	/**
	 * Управляющий сигнал 19: БР -> РД
	 */
	BUF_TO_DATA,
	/**
	 * Управляющий сигнал 20: БР -> РК
	 */
	BUF_TO_INSTR,
	/**
	 * Управляющий сигнал 21: БР -> СК
	 */
	BUF_TO_IP,
	/**
	 * Управляющий сигнал 22: БР -> А
	 */
	BUF_TO_ACCUM,
	/**
	 * Управляющий сигнал 23: Память -> РД
	 */
	MEMORY_READ,
	/**
	 * Управляющий сигнал 24: РД -> Память
	 */
	MEMORY_WRITE,
	/**
	 * Управляющий сигнал 25: Ввод-вывод
	 */
	INPUT_OUTPUT,
	/**
	 * Управляющий сигнал 26: Очистка всех флагов
	 * @deprecated Не используется
	 */
	CLEAR_ALL_FLAGS,
	/**
	 * Управляющий сигнал 27: DI
	 */
	DISABLE_INTERRUPTS,
	/**
	 * Управляющий сигнал 28: EI
	 */
	ENABLE_INTERRUPTS,
	/**
	 * Псевдосигнал: Переключение работа/останов
	 */
	SET_RUN_STATE,
	/**
	 * Псевдосигнал: Установка программы
	 */
	SET_PROGRAM,
	/**
	 * Псевдосигнал: Запрос прерывания
	 */
	SET_REQUEST_INTERRUPT,
	/**
	 * Псевдосигнал: TSF 1
	 */
	IO0_TSF,
	/**
	 * Псевдосигнал: Установка флага ВУ1
	 */
	IO1_SETFLAG,
	/**
	 * Псевдосигнал: TSF 1
	 */
	IO1_TSF,
	/**
	 * Псевдосигнал: OUT 1
	 */
	IO1_OUT,
	/**
	 * Псевдосигнал: Установка флага ВУ2
	 */
	IO2_SETFLAG,
	/**
	 * Псевдосигнал: TSF 2
	 */
	IO2_TSF,
	/**
	 * Псевдосигнал: IN 2
	 */
	IO2_IN,
	/**
	 * Псевдосигнал: Установка флага ВУ3
	 */
	IO3_SETFLAG,
	/**
	 * Псевдосигнал: TSF 3
	 */
	IO3_TSF,
	/**
	 * Псевдосигнал: IN 3
	 */
	IO3_IN,
	/**
	 * Псевдосигнал: OUT 3
	 */
	IO3_OUT
}
